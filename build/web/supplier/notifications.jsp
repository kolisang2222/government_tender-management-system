<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Notifications - ProcureGov</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/ministry-style.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/supplierdashboard.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/supplier-notifications.css">
        <link rel="stylesheet" href="procgov-dashboard.css">
    </head>
    <body>

        <jsp:include page="/shared/navigation.jsp" />

        <div class="dashboard-layout">

            <jsp:include page="/shared/sidebar.jsp" />

            <main class="dashboard-main">

                <!-- Page Header -->
                <div class="page-header">
                    <div class="header-left">
                        <h1>Notifications</h1>
                        <p>Stay updated on tender activities and bid statuses</p>
                    </div>
                    <div class="header-right">
                        <c:if test="${not empty notifications}">
                            <form action="${pageContext.request.contextPath}/supplier/notifications" method="POST" style="display: inline;">
                                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                                <input type="hidden" name="action" value="markAllRead">
                                <button type="submit" class="btn btn-outline">Mark All as Read</button>
                            </form>
                        </c:if>
                    </div>
                </div>

                <!-- Success/Error Messages -->
                <c:if test="${not empty sessionScope.successMessage}">
                    <div class="alert alert-success">
                        <span>OK</span>
                        <span>${sessionScope.successMessage}</span>
                        <button class="close-alert" onclick="this.parentElement.remove()">&times;</button>
                    </div>
                    <c:remove var="successMessage" scope="session" />
                </c:if>

                <c:if test="${not empty sessionScope.errorMessage}">
                    <div class="alert alert-error">
                        <span>!</span>
                        <span>${sessionScope.errorMessage}</span>
                        <button class="close-alert" onclick="this.parentElement.remove()">&times;</button>
                    </div>
                    <c:remove var="errorMessage" scope="session" />
                </c:if>

                <!-- Stats Summary -->
                <div class="stats-summary">
                    <div class="stat-card">
                        <div class="stat-icon">T</div>
                        <div class="stat-content">
                            <span class="stat-value">${totalNotifications}</span>
                            <span class="stat-label">Total Notifications</span>
                        </div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-icon">U</div>
                        <div class="stat-content">
                            <span class="stat-value">${unreadCount}</span>
                            <span class="stat-label">Unread</span>
                        </div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-icon">A</div>
                        <div class="stat-content">
                            <span class="stat-value">${awardCount}</span>
                            <span class="stat-label">Award Notices</span>
                        </div>
                    </div>
                </div>

                <!-- Filter Tabs -->
                <div class="notification-tabs">
                    <a href="${pageContext.request.contextPath}/supplier/notifications" 
                       class="tab-link ${empty param.filter ? 'active' : ''}">
                        All Notifications
                    </a>
                    <a href="${pageContext.request.contextPath}/supplier/notifications?filter=unread" 
                       class="tab-link ${param.filter == 'unread' ? 'active' : ''}">
                        Unread
                        <c:if test="${unreadCount > 0}">
                            <span class="tab-count">${unreadCount}</span>
                        </c:if>
                    </a>
                    <a href="${pageContext.request.contextPath}/supplier/notifications?filter=award" 
                       class="tab-link ${param.filter == 'award' ? 'active' : ''}">
                        Award Notices
                    </a>
                    <a href="${pageContext.request.contextPath}/supplier/notifications?filter=tender" 
                       class="tab-link ${param.filter == 'tender' ? 'active' : ''}">
                        Tender Updates
                    </a>
                    <a href="${pageContext.request.contextPath}/supplier/notifications?filter=bid" 
                       class="tab-link ${param.filter == 'bid' ? 'active' : ''}">
                        Bid Updates
                    </a>
                </div>

                <!-- Notifications List -->
                <div class="notifications-container">
                    <c:choose>
                        <c:when test="${not empty notifications}">
                            <c:forEach var="notification" items="${notifications}">
                                <div class="notification-card ${notification.read ? 'read' : 'unread'} type-${notification.type}">
                                    <div class="notification-icon">
                                        <c:choose>
                                            <c:when test="${notification.type == 'award_won'}">W</c:when>
                                            <c:when test="${notification.type == 'award_lost'}">L</c:when>
                                            <c:when test="${notification.type == 'tender_new'}">N</c:when>
                                            <c:when test="${notification.type == 'tender_closing'}">C</c:when>
                                            <c:when test="${notification.type == 'bid_submitted'}">B</c:when>
                                            <c:when test="${notification.type == 'evaluation_complete'}">E</c:when>
                                            <c:otherwise>!</c:otherwise>
                                        </c:choose>
                                    </div>

                                    <div class="notification-content">
                                        <div class="notification-header">
                                            <span class="notification-title">${notification.title}</span>
                                            <span class="notification-time">${notification.timeAgo}</span>
                                        </div>
                                        <p class="notification-message">${notification.message}</p>

                                        <c:if test="${not empty notification.tenderReference}">
                                            <div class="notification-meta">
                                                <span class="meta-label">Tender:</span>
                                                <span class="meta-value">${notification.tenderReference}</span>
                                            </div>
                                        </c:if>
                                    </div>

                                    <div class="notification-actions">
                                        <c:if test="${not notification.read}">
                                            <form action="${pageContext.request.contextPath}/supplier/notifications" method="POST" style="display: inline;">
                                                <input type="hidden" name="action" value="markRead">
                                                <input type="hidden" name="notificationId" value="${notification.notificationId}">
                                                <button type="submit" class="btn-icon" title="Mark as Read">OK</button>
                                            </form>
                                        </c:if>

                                        <c:if test="${not empty notification.link}">
                                            <a href="${pageContext.request.contextPath}${notification.link}" class="btn-icon" title="View Details">View</a>
                                        </c:if>

                                        <form action="${pageContext.request.contextPath}/supplier/notifications" method="POST" style="display: inline;">
                                            <input type="hidden" name="action" value="delete">
                                            <input type="hidden" name="notificationId" value="${notification.notificationId}">
                                            <button type="submit" class="btn-icon btn-danger" title="Delete" 
                                                    onclick="return confirm('Delete this notification?');">X</button>
                                        </form>
                                    </div>
                                </div>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <div class="empty-state">
                                <div class="empty-icon">!</div>
                                <h3>No Notifications</h3>
                                <p>You don't have any notifications at this time.</p>
                                <a href="${pageContext.request.contextPath}/supplier/tenders" class="btn btn-primary">
                                    Browse Open Tenders
                                </a>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>

                <!-- Pagination -->
                <c:if test="${totalPages > 1}">
                    <div class="pagination">
                        <c:if test="${currentPage > 1}">
                            <a href="${pageContext.request.contextPath}/supplier/notifications?page=${currentPage - 1}&filter=${param.filter}" 
                               class="page-link">Previous</a>
                        </c:if>

                        <c:forEach begin="1" end="${totalPages}" var="page">
                            <c:choose>
                                <c:when test="${page == currentPage}">
                                    <span class="page-link active">${page}</span>
                                </c:when>
                                <c:otherwise>
                                    <a href="${pageContext.request.contextPath}/supplier/notifications?page=${page}&filter=${param.filter}" 
                                       class="page-link">${page}</a>
                                </c:otherwise>
                            </c:choose>
                        </c:forEach>

                        <c:if test="${currentPage < totalPages}">
                            <a href="${pageContext.request.contextPath}/supplier/notifications?page=${currentPage + 1}&filter=${param.filter}" 
                               class="page-link">Next</a>
                        </c:if>
                    </div>
                </c:if>

                <!-- Delete All Read Notifications -->
                <c:if test="${not empty notifications}">
                    <div class="delete-all-section">
                        <form action="${pageContext.request.contextPath}/supplier/notifications" method="POST" 
                              onsubmit="return confirm('Delete all read notifications? This cannot be undone.');">
                            <input type="hidden" name="action" value="deleteAllRead">
                            <button type="submit" class="btn btn-danger">Delete All Read Notifications</button>
                        </form>
                    </div>
                </c:if>

            </main>
        </div>

        <jsp:include page="/shared/footer.jsp" />

        <script>
            setTimeout(function () {
                const alerts = document.querySelectorAll('.alert');
                alerts.forEach(function (alert) {
                    alert.style.transition = 'opacity 0.5s';
                    alert.style.opacity = '0';
                    setTimeout(function () {
                        alert.remove();
                    }, 500);
                });
            }, 5000);
        </script>

    </body>
</html>