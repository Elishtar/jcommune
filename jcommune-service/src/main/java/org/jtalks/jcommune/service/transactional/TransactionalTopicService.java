/**
 * Copyright (C) 2011  JTalks.org Team
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.jtalks.jcommune.service.transactional;

import org.joda.time.DateTime;
import org.jtalks.jcommune.model.dao.BranchDao;
import org.jtalks.jcommune.model.dao.TopicDao;
import org.jtalks.jcommune.model.entity.Branch;
import org.jtalks.jcommune.model.entity.Post;
import org.jtalks.jcommune.model.entity.Topic;
import org.jtalks.jcommune.model.entity.User;
import org.jtalks.jcommune.service.BranchService;
import org.jtalks.jcommune.service.SecurityService;
import org.jtalks.jcommune.service.TopicService;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.jtalks.jcommune.service.security.SecurityConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;

import javax.servlet.http.HttpSession;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Topic service class. This class contains method needed to manipulate with Topic persistent entity.
 *
 * @author Osadchuck Eugeny
 * @author Vervenko Pavel
 * @author Kirill Afonin
 * @author Vitaliy Kravchenko
 * @author Max Malakhov
 */
public class TransactionalTopicService extends AbstractTransactionalEntityService<Topic, TopicDao>
        implements TopicService {
    
    private final Logger logger = LoggerFactory.getLogger(getClass());
    public static final String TOPICS_VIEWED_ATTRIBUTE_NAME = "topicsViewed";
    
    private final SecurityService securityService;
    private BranchService branchService;
    private BranchDao branchDao;

    /**
     * Create an instance of User entity based service
     *
     * @param dao             data access object, which should be able do all CRUD operations with topic entity
     * @param securityService {@link SecurityService} for retrieving current user
     * @param branchService   {@link org.jtalks.jcommune.service.BranchService} instance to be injected
     * @param branchDao       used for checking branch existence
     */
    public TransactionalTopicService(TopicDao dao, SecurityService securityService,
                                     BranchService branchService, BranchDao branchDao) {
        super(dao);
        this.securityService = securityService;
        this.branchService = branchService;
        this.branchDao = branchDao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @PreAuthorize("hasAnyRole('" + SecurityConstants.ROLE_USER + "','" + SecurityConstants.ROLE_ADMIN + "')")
    public Post replyToTopic(long topicId, String answerBody) throws NotFoundException {
        User currentUser = securityService.getCurrentUser();
        if (currentUser == null) { // it shouldn't happen because only registered user can have this roles
            String msg = "User should log in to post answers.";
            logger.error(msg);
            throw new IllegalStateException(msg);
        }

        Topic topic = get(topicId);
        Post answer = new Post(currentUser, answerBody);
        topic.addPost(answer);
        topic.updateModificationDate();
        this.getDao().update(topic);

        securityService.grantToCurrentUser().role(SecurityConstants.ROLE_ADMIN).admin().on(answer);

        logger.debug("New post in topic. Topic id={}, Post id={}, Post author={}", 
                new Object[]{ topicId, answer.getId(), currentUser.getUsername() });
        
        return answer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @PreAuthorize("hasAnyRole('" + SecurityConstants.ROLE_USER + "','" + SecurityConstants.ROLE_ADMIN + "')")
    public Topic createTopic(String topicName, String bodyText, long branchId) throws NotFoundException {
        User currentUser = securityService.getCurrentUser();
        if (currentUser == null) { // it shouldn't happen because only registered user can have this roles
            String msg = "User should log in to create topic.";
            logger.error(msg);
            throw new IllegalStateException(msg);
        }

        Branch branch = branchService.get(branchId);
        Topic topic = new Topic(currentUser, topicName);
        Post first = new Post(currentUser, bodyText);
        topic.addPost(first);
        branch.addTopic(topic);
        branchDao.update(branch);

        securityService.grantToCurrentUser().role(SecurityConstants.ROLE_ADMIN).admin().on(topic)
                .user(currentUser.getUsername()).role(SecurityConstants.ROLE_ADMIN).admin().on(first);
        
        logger.debug("Created new topic id={}, branch id={}, author={}", 
                    new Object[]{ topic.getId(), branchId, currentUser.getUsername() });
        logger.info("Created new topic: \"{}\". Author: {}", topicName, currentUser.getUsername());
        
        return topic;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Topic> getTopicRangeInBranch(long branchId, int start, int max) throws NotFoundException {
        if (!branchDao.isExist(branchId)) {
            throw new NotFoundException("Branch with id: " + branchId + " not found");
        }
        return this.getDao().getTopicRangeInBranch(branchId, start, max);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Topic> getAllTopicsPastLastDay(int start, int max, DateTime lastLogin) {
        if (lastLogin == null) {
            lastLogin = new DateTime().minusDays(1);
        }
        return this.getDao().getAllTopicsPastLastDay(start, max, lastLogin);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getTopicsInBranchCount(long branchId) throws NotFoundException {
        if (!branchDao.isExist(branchId)) {
            throw new NotFoundException("Branch with id: " + branchId + " not found");
        }
        return this.getDao().getTopicsInBranchCount(branchId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getTopicsPastLastDayCount(DateTime lastLogin) {
        if (lastLogin == null) {
            lastLogin = new DateTime().minusDays(1);
        }
        return this.getDao().getTopicsPastLastDayCount(lastLogin);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @PreAuthorize("hasPermission(#topicId, 'org.jtalks.jcommune.model.entity.Topic', admin)")
    public void updateTopic(long topicId, String topicName, String bodyText)
            throws NotFoundException {
        updateTopic(topicId, topicName, bodyText, 0, false, false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @PreAuthorize("hasPermission(#topicId, 'org.jtalks.jcommune.model.entity.Topic', admin)")
    public void updateTopic(long topicId, String topicName, String bodyText, int topicWeight,
                          boolean sticked, boolean announcement)
            throws NotFoundException {
        Topic topic = get(topicId);
        topic.setTitle(topicName);
        topic.setTopicWeight(topicWeight);
        topic.setSticked(sticked);
        topic.setAnnouncement(announcement);
        Post post = topic.getFirstPost();
        post.setPostContent(bodyText);
        topic.updateModificationDate();
        this.getDao().update(topic);
        
        logger.debug("Topic id={} updated", topic.getId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @PreAuthorize("hasPermission(#topicId, 'org.jtalks.jcommune.model.entity.Topic', admin) or " +
            "hasPermission(#topicId, 'org.jtalks.jcommune.model.entity.Topic', delete)")
    public Branch deleteTopic(long topicId) throws NotFoundException {
        Topic topic = get(topicId);
        Branch branch = topic.getBranch();
        branch.deleteTopic(topic);
        branchDao.update(branch);

        securityService.deleteFromAcl(Topic.class, topicId);
        
        logger.info("Deleted topic \"{}\". Topic id: {}", topic.getTitle(), topicId);
        
        return branch;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addTopicView(Topic topic, HttpSession session) throws NotFoundException {
        if (session == null) {
            throw new IllegalArgumentException("Session cannot be null");
        }
        Set<Long> topicIds = (Set<Long>) session.getAttribute(TOPICS_VIEWED_ATTRIBUTE_NAME);
        if (topicIds == null) {
            topicIds = new HashSet<Long>();
        }
        if (!topicIds.contains(topic.getId())) {
            topic.setViews(topic.getViews() + 1);
            this.getDao().update(topic);
        }
        topicIds.add(topic.getId());
        session.setAttribute(TOPICS_VIEWED_ATTRIBUTE_NAME, topicIds);
    }
}
