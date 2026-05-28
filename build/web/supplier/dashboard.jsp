<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Supplier Dashboard - ProcureGov</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/ministry-style.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/supplierdashboard.css">
        <link rel="stylesheet" href="procgov-dashboard.css">
    </head>
    <body>

        <jsp:include page="/shared/navigation.jsp" />

        <div class="dashboard-layout">

            <jsp:include page="/shared/sidebar.jsp" />

            <main class="dashboard-main">

                <!-- Page Header -->
                <div class="page-header">
                    <h1>Supplier Dashboard</h1>
                    <p>Welcome, ${sessionScope.userName}! Browse open tenders and manage your bids.</p>
                </div>

                <!-- Message Display -->
                <c:if test="${not empty param.success}">
                    <div class="alert alert-success">
                        <span>OK</span>
                        <span>${param.success}</span>
                        <button class="close-alert" onclick="this.parentElement.remove()">&times;</button>
                    </div>
                </c:if>
                <c:if test="${not empty param.error}">
                    <div class="alert alert-error">
                        <span>!</span>
                        <span>${param.error}</span>
                        <button class="close-alert" onclick="this.parentElement.remove()">&times;</button>
                    </div>
                </c:if>

                <c:if test="${not empty sessionScope.successMessage}">
                    <div class="alert alert-success">
                        <span>OK</span>
                        <span>${sessionScope.successMessage}</span>
                        <button class="close-alert" onclick="this.parentElement.remove()">&times;</button>
                    </div>
                    <c:remove var="successMessage" scope="session" />
                </c:if>

                <!-- Stats Cards -->
                <div class="stats-container">
                    <div class="stat-card">
                        <div class="stat-value">${openTendersCount}</div>
                        <div class="stat-label">Open Tenders</div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-value">${myBidsCount}</div>
                        <div class="stat-label">My Bids</div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-value">${wonBidsCount}</div>
                        <div class="stat-label">Won Bids</div>
                    </div>
                </div>

                <!-- Dashboard Grid -->
                <div class="dashboard-grid">

                    <!-- Left Column: Open Tenders -->
                    <div class="dashboard-card">
                        <div class="card-header">
                            <h3>Open Tenders</h3>
                            <a href="${pageContext.request.contextPath}/supplier/tenders" class="view-all">View All</a>
                        </div>
                        <div class="card-body">
                            <c:choose>
                                <c:when test="${not empty openTenders}">
                                    <table class="data-table">
                                        <thead>
                                            <tr>
                                                <th>Reference</th>
                                                <th>Title</th>
                                                <th>Category</th>
                                                <th>Closing Date</th>
                                                <th>Action</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach items="${openTenders}" var="tender" begin="0" end="4">
                                                <tr>
                                                    <td>${tender.referenceNumber}</td>
                                                    <td>${tender.title}</td>
                                                    <td>${tender.category.displayName}</td>
                                                    <td>${tender.formattedClosingDateTime}</td>
                                                    <td>
                                                        <a href="${pageContext.request.contextPath}/supplier/tender-detail?id=${tender.tenderId}" 
                                                           class="btn-action">View & Bid</a>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </tbody>
                                    </table>
                                </c:when>
                                <c:otherwise>
                                    <p class="empty-state">No open tenders available at this time.</p>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>

                    <!-- Right Column: My Recent Bids -->
                    <div class="dashboard-card">
                        <div class="card-header">
                            <h3>My Recent Bids</h3>
                            <a href="${pageContext.request.contextPath}/supplier/bids" class="view-all">View All</a>
                        </div>
                        <div class="card-body">
                            <c:choose>
                                <c:when test="${not empty myBids}">
                                    <table class="data-table">
                                        <thead>
                                            <tr>
                                                <th>Tender</th>
                                                <th>Bid Amount</th>
                                                <th>Status</th>
                                                <th>Submitted</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach items="${myBids}" var="bid" begin="0" end="4">
                                                <tr>
                                                    <td>${bid.tenderReference}</td>
                                                    <td>${bid.formattedBidAmount}</td>
                                                    <td>
                                                        <span class="status-badge status-${bid.tenderStatus.name().toLowerCase()}">
                                                            ${bid.tenderStatus.displayName}
                                                        </span>
                                                    </td>
                                                    <td>${bid.formattedSubmittedAt}</td>
                                                </tr>
                                            </c:forEach>
                                        </tbody>
                                    </table>
                                </c:when>
                                <c:otherwise>
                                    <p class="empty-state">You haven't submitted any bids yet.</p>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>

                    <!-- Award Notices - Full Width -->
                    <div class="dashboard-card full-width">
                        <div class="card-header">
                            <h3>Award Notices</h3>
                        </div>
                        <div class="card-body">
                            <c:choose>
                                <c:when test="${not empty awardNotices}">
                                    <table class="data-table">
                                        <thead>
                                            <tr>
                                                <th>Tender Reference</th>
                                                <th>Title</th>
                                                <th>Result</th>
                                                <th>Award Date</th>
                                                <th>Action</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach items="${awardNotices}" var="notice">
                                                <tr>
                                                    <td>${notice.tenderReference}</td>
                                                    <td>${notice.tenderTitle}</td>
                                                    <td>
                                                        <c:choose>
                                                            <c:when test="${notice.won}">
                                                                <span class="badge-success">Won</span>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <span class="badge-secondary">Not Awarded</span>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </td>
                                                    <td>${notice.formattedAwardDate}</td>
                                                    <td>
                                                        <a href="${pageContext.request.contextPath}/supplier/award-notice?id=${notice.tenderId}" 
                                                           class="btn-action">View Notice</a>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </tbody>
                                    </table>
                                </c:when>
                                <c:otherwise>
                                    <p class="empty-state">No award notices available.</p>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>

                    <!-- Upcoming Deadlines -->
                    <div class="dashboard-card full-width">
                        <div class="card-header">
                            <h3>Upcoming Deadlines</h3>
                        </div>
                        <div class="card-body">
                            <c:choose>
                                <c:when test="${not empty upcomingDeadlines}">
                                    <table class="data-table">
                                        <thead>
                                            <tr>
                                                <th>Tender Reference</th>
                                                <th>Title</th>
                                                <th>Closing Date</th>
                                                <th>Time Remaining</th>
                                                <th>Action</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach items="${upcomingDeadlines}" var="tender">
                                                <tr>
                                                    <td>${tender.referenceNumber}</td>
                                                    <td>${tender.title}</td>
                                                    <td>${tender.formattedClosingDateTime}</td>
                                                    <td>${tender.timeRemaining}</td>
                                                    <td>
                                                        <a href="${pageContext.request.contextPath}/supplier/tender-detail?id=${tender.tenderId}" 
                                                           class="btn-action">Bid Now</a>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </tbody>
                                    </table>
                                </c:when>
                                <c:otherwise>
                                    <p class="empty-state">No upcoming deadlines.</p>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>

                </div>
            </main>
        </div>

        <jsp:include page="/shared/footer.jsp" />

        <script>
            // Close alert messages after 5 seconds
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