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
package org.jtalks.jcommune.service;

import java.util.List;

/**
 * This interface declare methods for getting forum statistic information.
 *
 * @author Elena Lepaeva
 */
public interface ForumStatisticsService {
    /**
     * Get total count of messages on the forum
     *
     * @return number of posts on the forum.
     */
    int getPostsOnForumCount();

    /**
     * Return total count of registred user's accounts
     *
     * @return count of registred user's accounts
     */
    int getUsersCount();

    /**
     * Return list of registered users who is online now
     *
     * @return list of users
     */
    List<Object> getOnlineRegisteredUsers();

    /**
     * Return total number of online users
     *
     * @return total number of online users
     */
    long getOnlineUsersCount();

    /**
     * Return number of online registered users
     *
     * @return number of users
     */
    long getOnlineRegisteredUsersCount();

    /**
     * Return number of online anonymous users
     *
     * @return number of users
     */
    long getOnlineAnonymoustUsersCount();

}
