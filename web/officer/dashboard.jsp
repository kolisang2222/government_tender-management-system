<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Officer Dashboard - ProcureGov</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/officerdashboard.css">
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
                    <h1>Dashboard</h1>
                    <p>Welcome back, ${sessionScope.userName}</p>
                </div>
                <div class="header-right">
                    <a href="${pageContext.request.contextPath}/officer/create-tender" class="btn btn-primary">
                        Create New Tender
                    </a>
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

            <!-- Stats Overview Cards -->
            <div class="stats-overview">
                <div class="stat-card">
                    <div class="stat-icon blue">
                        <span>D</span>
                    </div>
                    <div class="stat-content">
                        <span class="stat-value">${draftTenders}</span>
                        <span class="stat-label">Draft Tenders</span>
                    </div>
                </div>
                <div class="stat-card">
                    <div class="stat-icon green">
                        <span>O</span>
                    </div>
                    <div class="stat-content">
                        <span class="stat-value">${openTenders}</span>
                        <span class="stat-label">Open Tenders</span>
                    </div>
                </div>
                <div class="stat-card">
                    <div class="stat-icon orange">
                        <span>E</span>
                    </div>
                    <div class="stat-content">
                        <span class="stat-value">${underEvaluationTenders}</span>
                        <span class="stat-label">Under Evaluation</span>
                    </div>
                </div>
                <div class="stat-card">
                    <div class="stat-icon purple">
                        <span>A</span>
                    </div>
                    <div class="stat-content">
                        <span class="stat-value">${awardedTenders}</span>
                        <span class="stat-label">Awarded</span>
                    </div>
                </div>
            </div>

            <!-- Quick Stats Row -->
            <div class="quick-stats">
                <div class="quick-stat-item">
                    <span class="quick-stat-label">Total Tenders</span>
                    <span class="quick-stat-value">${totalTenders}</span>
                </div>
                <div class="quick-stat-item">
                    <span class="quick-stat-label">Closed Tenders</span>
                    <span class="quick-stat-value">${closedTenders}</span>
                </div>
                <div class="quick-stat-item">
                    <span class="quick-stat-label">Evaluated</span>
                    <span class="quick-stat-value">${evaluatedTenders}</span>
                </div>
                <div class="quick-stat-item">
                    <span class="quick-stat-label">My Tenders</span>
                    <span class="quick-stat-value">${myTendersCount}</span>
                </div>
                <div class="quick-stat-item">
                    <span class="quick-stat-label">Total Suppliers</span>
                    <span class="quick-stat-value">${totalSuppliers}</span>
                </div>
                <div class="quick-stat-item">
                    <span class="quick-stat-label">Total Award Value</span>
                    <span class="quick-stat-value">${totalAwardValue}</span>
                </div>
            </div>

            <!-- Main Dashboard Grid -->
            <div class="dashboard-grid">
                
                <!-- Left Column -->
                <div class="grid-left">
                    
                    <!-- Pending Tasks Card -->
                    <div class="dashboard-card">
                        <div class="card-header">
                            <h3>Pending Tasks</h3>
                        </div>
                        <div class="card-body">
                            <c:choose>
                                <c:when test="${not empty pendingEvaluations}">
                                    <div class="task-list">
                                        <c:forEach items="${pendingEvaluations}" var="tender" begin="0" end="3">
                                            <div class="task-item">
                                                <div class="task-icon warning">!</div>
                                                <div class="task-content">
                                                    <span class="task-title">${tender.referenceNumber}</span>
                                                    <span class="task-desc">${tender.title}</span>
                                                    <span class="task-meta">${tender.bidCount} bids waiting</span>
                                                </div>
                                                <a href="${pageContext.request.contextPath}/officer/tender-detail?id=${tender.tenderId}" 
                                                   class="task-link">View</a>
                                            </div>
                                        </c:forEach>
                                    </div>
                                    <c:if test="${pendingEvaluations.size() > 4}">
                                        <div class="card-footer-link">
                                            <a href="${pageContext.request.contextPath}/officer/evaluations">View all pending tasks</a>
                                        </div>
                                    </c:if>
                                </c:when>
                                <c:otherwise>
                                    <p class="empty-state-small">No pending tasks</p>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>

                    <!-- Ready for Award Card -->
                    <div class="dashboard-card">
                        <div class="card-header">
                            <h3>Ready for Award</h3>
                        </div>
                        <div class="card-body">
                            <c:choose>
                                <c:when test="${not empty readyForAward}">
                                    <div class="task-list">
                                        <c:forEach items="${readyForAward}" var="tender" begin="0" end="2">
                                            <div class="task-item">
                                                <div class="task-icon success">OK</div>
                                                <div class="task-content">
                                                    <span class="task-title">${tender.referenceNumber}</span>
                                                    <span class="task-desc">${tender.title}</span>
                                                </div>
                                                <a href="${pageContext.request.contextPath}/officer/award-tender?id=${tender.tenderId}" 
                                                   class="btn btn-sm btn-success">Award</a>
                                            </div>
                                        </c:forEach>
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <p class="empty-state-small">No tenders ready for award</p>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>

                </div>

                <!-- Right Column -->
                <div class="grid-right">
                    
                    <!-- Recent Activity Card -->
                    <div class="dashboard-card">
                        <div class="card-header">
                            <h3>Recent Activity</h3>
                        </div>
                        <div class="card-body">
                            <c:choose>
                                <c:when test="${not empty recentActivity}">
                                    <div class="activity-list">
                                        <c:forEach items="${recentActivity}" var="activity" begin="0" end="4">
                                            <div class="activity-item">
                                                <div class="activity-icon ${activity.type}">
                                                    <c:choose>
                                                        <c:when test="${activity.type == 'tender_created'}">N</c:when>
                                                        <c:when test="${activity.type == 'tender_awarded'}">A</c:when>
                                                        <c:otherwise>!</c:otherwise>
                                                    </c:choose>
                                                </div>
                                                <div class="activity-content">
                                                    <span class="activity-title">${activity.title}</span>
                                                    <span class="activity-desc">${activity.description}</span>
                                                    <span class="activity-time">${activity.formattedTime}</span>
                                                </div>
                                            </div>
                                        </c:forEach>
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <p class="empty-state-small">No recent activity</p>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>

                    <!-- Upcoming Deadlines Card -->
                    <div class="dashboard-card">
                        <div class="card-header">
                            <h3>Upcoming Deadlines</h3>
                        </div>
                        <div class="card-body">
                            <c:choose>
                                <c:when test="${not empty upcomingDeadlines}">
                                    <div class="deadline-list">
                                        <c:forEach items="${upcomingDeadlines}" var="tender" begin="0" end="3">
                                            <div class="deadline-item">
                                                <div class="deadline-info">
                                                    <span class="deadline-ref">${tender.referenceNumber}</span>
                                                    <span class="deadline-title">${tender.title}</span>
                                                </div>
                                                <div class="deadline-time ${tender.urgent ? 'urgent' : ''}">
                                                    ${tender.timeRemaining}
                                                </div>
                                            </div>
                                        </c:forEach>
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <p class="empty-state-small">No upcoming deadlines</p>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>

                </div>
            </div>

            <!-- Recent Tenders Table -->
            <div class="dashboard-card full-width">
                <div class="card-header">
                    <h3>Recent Tenders</h3>
                    <a href="${pageContext.request.contextPath}/officer/tenders" class="view-all">View All</a>
                </div>
                <div class="card-body">
                    <c:choose>
                        <c:when test="${not empty recentTenders}">
                            <table class="data-table">
                                <thead>
                                    <tr>
                                        <th>Reference</th>
                                        <th>Title</th>
                                        <th>Category</th>
                                        <th>Status</th>
                                        <th>Closing Date</th>
                                        <th>Bids</th>
                                        <th>Actions</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach items="${recentTenders}" var="tender">
                                        <tr>
                                            <td><span class="ref-number">${tender.referenceNumber}</span></td>
                                            <td>${tender.title}</td>
                                            <td>${tender.category.displayName}</td>
                                            <td>
                                                <span class="status-badge status-${tender.status.name().toLowerCase()}">
                                                    ${tender.status.displayName}
                                                </span>
                                            </td>
                                            <td>${tender.formattedClosingDateTime}</td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${tender.bidCount > 0}">
                                                        <span class="bid-count">${tender.bidCount} bids</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="bid-count zero">0 bids</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td class="actions-cell">
                                                <a href="${pageContext.request.contextPath}/officer/tender-detail?id=${tender.tenderId}" 
                                                   class="btn-icon" title="View Details">View</a>
                                                
                                                <c:if test="${tender.status == 'DRAFT'}">
                                                    <a href="${pageContext.request.contextPath}/officer/edit-tender?id=${tender.tenderId}" 
                                                       class="btn-icon" title="Edit">Edit</a>
                                                </c:if>
                                                
                                                <c:if test="${tender.status == 'EVALUATED'}">
                                                    <a href="${pageContext.request.contextPath}/officer/award-tender?id=${tender.tenderId}" 
                                                       class="btn-icon btn-success" title="Award">Award</a>
                                                </c:if>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </c:when>
                        <c:otherwise>
                            <p class="empty-state">No tenders found. Create your first tender!</p>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>

        </main>
    </div>

    <jsp:include page="/shared/footer.jsp" />

    <script>
        // Close alert messages after 5 seconds
        setTimeout(function() {
            const alerts = document.querySelectorAll('.alert');
            alerts.forEach(function(alert) {
                alert.style.transition = 'opacity 0.5s';
                alert.style.opacity = '0';
                setTimeout(function() {
                    alert.remove();
                }, 500);
            });
        }, 5000);
    </script>

</body>
</html>