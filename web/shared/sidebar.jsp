<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/sidebar.css">

<aside>
    <c:choose>
        <c:when test="${sessionScope.userRole == 'SUPPLIER'}">
            <div>
                <h4>Quick Stats</h4>
                <div>
                    <div>
                        <span>📋</span>
                        <div>
                            <span>Open Tenders</span>
                            <span>${openTendersCount}</span>
                        </div>
                    </div>
                    <div>
                        <span>📤</span>
                        <div>
                            <span>My Bids</span>
                            <span>${myBidsCount}</span>
                        </div>
                    </div>
                    <div>
                        <span>✅</span>
                        <div>
                            <span>Won Bids</span>
                            <span>${wonBidsCount}</span>
                        </div>
                    </div>
                </div>
            </div>

            <div>
                <h4>Upcoming Deadlines</h4>
                <c:if test="${not empty upcomingDeadlines}">
                    <ul>
                        <c:forEach var="tender" items="${upcomingDeadlines}">
                            <li>
                                <strong>${tender.referenceNumber}</strong>
                                <span>${tender.title}</span>
                                <small>Closes: ${tender.formattedClosingDate}</small>
                            </li>
                        </c:forEach>
                    </ul>
                </c:if>
                <c:if test="${empty upcomingDeadlines}">
                    <p>No upcoming deadlines</p>
                </c:if>
            </div>

            <div>
                <h4>Recent Notifications</h4>
                <c:if test="${not empty recentNotifications}">
                    <ul>
                        <c:forEach var="notification" items="${recentNotifications}">
                            <li>
                                <span>${notification.message}</span>
                                <small>${notification.timeAgo}</small>
                            </li>
                        </c:forEach>
                    </ul>
                </c:if>
                <c:if test="${empty recentNotifications}">
                    <p>No new notifications</p>
                </c:if>
            </div>
        </c:when>

        <c:when test="${sessionScope.userRole == 'PROCUREMENT_OFFICER'}">
            <div>
                <h4>Quick Actions</h4>
                <ul>
                    <li><a href="${pageContext.request.contextPath}/officer/create-tender">Create New Tender</a></li>
                    <li><a href="${pageContext.request.contextPath}/officer/tenders?status=DRAFT">View Drafts</a></li>
                    <li><a href="${pageContext.request.contextPath}/officer/evaluations?filter=pending">Pending Evaluations</a></li>
                    <li><a href="${pageContext.request.contextPath}/officer/reports">Generate Report</a></li>
                </ul>
            </div>

            <div>
                <h4>Tender Summary</h4>
                <div>
                    <div>
                        <div>
                            <span>Draft</span>
                            <span>${draftTendersCount}</span>
                        </div>
                    </div>
                    <div>
                        <div>
                            <span>Open</span>
                            <span>${openTendersCount}</span>
                        </div>
                    </div>
                    <div>
                        <div>
                            <span>Closed</span>
                            <span>${closedTendersCount}</span>
                        </div>
                    </div>
                    <div>
                        <div>
                            <span>Under Evaluation</span>
                            <span>${evaluationTendersCount}</span>
                        </div>
                    </div>
                    <div>
                        <div>
                            <span>Awarded</span>
                            <span>${awardedTendersCount}</span>
                        </div>
                    </div>
                </div>
            </div>

        </c:when>

        <c:when test="${sessionScope.userRole == 'EVALUATION_COMMITTEE'}">
            <div>
                <h4>My Assignments</h4>
                <c:if test="${not empty assignedTenders}">
                    <ul>
                        <c:forEach var="tender" items="${assignedTenders}">
                            <li>
                                <strong>${tender.referenceNumber}</strong>
                                <span>${tender.title}</span>
                                <c:if test="${tender.evaluationComplete}">
                                    <span class="status-completed">Completed</span>
                                </c:if>
                                <c:if test="${!tender.evaluationComplete}">
                                    <span class="status-pending">Pending</span>
                                </c:if>
                            </li>
                        </c:forEach>
                    </ul>
                </c:if>
                <c:if test="${empty assignedTenders}">
                    <p>No assignments currently</p>
                </c:if>
            </div>

            <div>
                <h4>Evaluation Progress</h4>
                <div>
                    <div>
                        <span>Total Assigned</span>
                        <span>${totalAssignedTenders}</span>
                    </div>
                    <div>
                        <span>Completed</span>
                        <span>${completedEvaluationsCount}</span>
                    </div>
                    <div>
                        <span>Pending</span>
                        <span>${pendingEvaluationsCount}</span>
                    </div>
                </div>
            </div>

            <div>
                <h4>Recent Results</h4>
                <c:if test="${not empty recentAwardedTenders}">
                    <ul>
                        <c:forEach var="tender" items="${recentAwardedTenders}">
                            <li>
                                <strong>${tender.referenceNumber}</strong>
                                <span>Awarded to: ${tender.winningSupplier}</span>
                                <small>${tender.awardDate}</small>
                            </li>
                        </c:forEach>
                    </ul>
                </c:if>
                <c:if test="${empty recentAwardedTenders}">
                    <p>No recent awards</p>
                </c:if>
            </div>
        </c:when>
    </c:choose>
</aside>