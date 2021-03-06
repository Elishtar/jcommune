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
package org.jtalks.jcommune.web.util;

import org.jtalks.jcommune.model.entity.User;
import org.jtalks.jcommune.service.PrivateMessageService;
import org.jtalks.jcommune.service.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Add user data that should be available on all pages.
 *
 * @author Kirill Afonin
 */
public class UserDataInterceptor extends HandlerInterceptorAdapter {
    private final PrivateMessageService service;
    private final SecurityService securityService;

    /**
     * Constructor
     *
     * @param service         service
     * @param securityService {@link SecurityService}
     */
    @Autowired
    public UserDataInterceptor(PrivateMessageService service, SecurityService securityService) {
        this.service = service;
        this.securityService = securityService;
    }

    /**
     * Can expose additional objects to the view.
     * Called after HandlerAdapter actually invoked the handler, but before the
     * DispatcherServlet renders the view.
     *
     * @param request      current HTTP request
     * @param response     current HTTP response
     * @param handler      chosen handler to execute, for type and/or instance examination
     * @param modelAndView the {@code ModelAndView} that the handler returned
     *                     (can also be {@code null})
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) {
        int newPmCount = service.currentUserNewPmCount();
        request.setAttribute("newPmCount", newPmCount);
        User user = securityService.getCurrentUser();

        String encodedUserName = user != null ? user.getEncodedUsername() : null;
        request.setAttribute("encodedUserName", encodedUserName);
    }
}
