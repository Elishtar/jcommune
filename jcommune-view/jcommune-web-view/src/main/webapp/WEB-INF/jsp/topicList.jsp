<%@ page import="org.jtalks.jcommune.web.util.Pagination" %>
<%--

    Copyright (C) 2011  JTalks.org Team
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU Lesser General Public
    License as published by the Free Software Foundation; either
    version 2.1 of the License, or (at your option) any later version.
    This library is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    Lesser General Public License for more details.
    You should have received a copy of the GNU Lesser General Public
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA

--%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="jtalks" uri="http://www.jtalks.org/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <title>Forum</title>
</head>
<body>
<h1>JTalks</h1>

<div class="wrap branch_page">
     <jsp:include page="../template/topLine.jsp"/>
    <!-- Начало всех форумов -->
    <div class="all_forums">
        <h2><a class="heading" href="#"><c:out value="${branch.name}"/></a></h2>
        <div class="forum_misc_info">
            <c:out value="${branch.description}"/>

            <span class="nav_top">На страницу: 1, <a href="#">2</a> <a href="#">След.</a></span>
        </div>
        <a class="forum_top_right_link" href="#">Отметить все темы как прочтенные</a>
        <sec:authorize access="hasAnyRole('ROLE_USER','ROLE_ADMIN')">
            <a class="button top_button"
               href="${pageContext.request.contextPath}/topics/new?branchId=${branchId}"><spring:message
                    code="label.addtopic"/></a>
            &nbsp; &nbsp; &nbsp;
        </sec:authorize>

        <jtalks:breadcrumb breadcrumbList="${breadcrumbList}"/>


        <!-- Начало группы форумов -->
        <div class="forum_header_table"> <!-- Шапка бранча -->
            <div class="forum_header">
                <span class="forum_header_topics"><spring:message code="label.branch.header.topics"/></span>
                <span class="forum_header_answers"><spring:message code="label.branch.header.answers"/></span>
                <span class="forum_header_author"><spring:message code="label.branch.header.author"/></span>
                <span class="forum_header_clicks"><spring:message code="label.branch.header.views"/></span>
                <span class="forum_header_last_message"><spring:message code="label.branch.header.lastMessage"/></span>
            </div>
        </div>


        <ul class="forum_table"> <!-- Список топиков -->
            <jtalks:display uri="${branchId}" currentPage="${page}" size="${size}" list="${topics}">
            <c:forEach var="topic" items="${list}">
                <li class="forum_row"> <!-- Топик -->
                    <div class="forum_icon"> <!-- Иконка с кофе -->
                        <img class="icon" src="${pageContext.request.contextPath}/resources/images/closed_cup.png"
                             alt=""
                             title="Форум закрыт"/>
                    </div>
                    <c:choose>
                        <c:when test="${topic.announcement=='true'}">
                            <div class="forum_info"> <!-- Ссылка на тему -->
                                <h4><span class="sticky"><spring:message code="label.marked_as_announcement"/> </span><a
                                        class="forum_link"
                                        href="${pageContext.request.contextPath}/topics/${topic.id}">
                                    <c:out value="${topic.title}"/></a></h4>
                            </div>
                        </c:when>
                        <c:when test="${topic.sticked=='true'}">
                            <div class="forum_info"> <!-- Ссылка на тему -->
                                <h4><span class="sticky"><spring:message code="label.marked_as_sticked"/> </span><a
                                        class="forum_link"
                                        href="${pageContext.request.contextPath}/topics/${topic.id}">
                                    <c:out value="${topic.title}"/></a></h4>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="forum_info"> <!-- Ссылка на тему -->
                                <h4><a class="forum_link"
                                       href="${pageContext.request.contextPath}/topics/${topic.id}"><c:out
                                        value="${topic.title}"/></a></h4>

                            </div>
                        </c:otherwise>
                    </c:choose>
                    <div class="forum_answers">
                        <c:out value="${topic.postCount}"/>
                    </div>
                    <div class="forum_author">
                        <a href="${pageContext.request.contextPath}/users/${topic.topicStarter.encodedUsername}"
                           title="Автор темы"><c:out value="${topic.topicStarter.username}"/></a>
                    </div>
                    <div class="forum_clicks">
                        <c:out value="${topic.views}"/>
                    </div>
                    <div class="forum_last_message">
                        <a href="${pageContext.request.contextPath}/topics/${topic.id}">
                            <jtalks:format value="${topic.lastPost.creationDate}"/></a>
                        <br/>
                        <a class="last_message_user"
                           href="${pageContext.request.contextPath}/users/${topic.lastPost.userCreated.encodedUsername}">
                            <c:out value="${topic.lastPost.userCreated.username}"/></a>
                        <a href="#"><img src="${pageContext.request.contextPath}/resources/images/icon_latest_reply.gif"
                                         alt="Последнее сообщение"/></a>
                    </div>
                </li>
            </c:forEach>
                <span class="nav_bottom"><spring:message code="label.onPage"/>
            </jtalks:display>
                    </span>
        </ul>

        <!-- Конец группы форумов -->



        <sec:authorize access="hasAnyRole('ROLE_USER','ROLE_ADMIN')">
            <a class="button"
               href="${pageContext.request.contextPath}/topics/new?branchId=${branchId}"><spring:message
                    code="label.addtopic"/></a>
            &nbsp; &nbsp; &nbsp;
        </sec:authorize>

        <c:if test="${maxPage>1}">
            <c:if test="${size==0 || size==2}">
                <sec:authorize access="hasAnyRole('ROLE_USER','ROLE_ADMIN')">
                    <a class="button"
                       href="?size=1"><spring:message code="label.showAll"/></a>
                    &nbsp; &nbsp; &nbsp;
                </sec:authorize>
            </c:if>
        </c:if>
        <c:if test="${size == 1}">
            <sec:authorize access="hasAnyRole('ROLE_USER','ROLE_ADMIN')">
                <a class="button"
                   href="?size=2"><spring:message code="label.showPages"/></a>
                &nbsp; &nbsp; &nbsp;
            </sec:authorize>
        </c:if>

        <jtalks:breadcrumb breadcrumbList="${breadcrumbList}"/>


        <div class="forum_misc_info">
            <spring:message code="label.page"/> <c:out value="${page}"/> <spring:message code="label.of"/> <c:out
                value="${maxPage}"/>
            <br/>
            Модераторы:
            <ul class="users_list">
                <li><a href="#">andreyko</a>,</li>
                <li><a href="#">Староверъ</a>,</li>
                <li><a href="#">Вася</a>.</li>
            </ul>
            <br/>
            Сейчас этот форум просматривают: Нет

        </div>
    </div>
    <!-- Конец всех форумов -->
    <div class="footer_buffer"></div>
    <!-- Несемантичный буфер для прибития подвала -->
</div>
</body>
</html>