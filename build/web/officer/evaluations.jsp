<%-- 
    Document   : evaluations
    Created on : Apr 15, 2026, 1:18:53 PM
    Author     : kolisang
--%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Evaluations - ProcureGov</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/ministry-style.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/officerdashboard.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/evaluations.css">
    </head>
    <body>

        <jsp:include page="/shared/navigation.jsp" />

        <div class="dashboard-layout">

            <jsp:include page="/shared/sidebar.jsp" />

            <main class="dashboard-main">

                <!-- Page Header -->
                <div class="page-header">
                    <div class="header-left">
                        <h1>⭐ Bid Evaluations</h1>
                        <p>Score and evaluate bids submitted for government tenders</p>
                    </div>
                </div>

                <!-- Success/Error Messages -->
                <c:if test="${not empty sessionScope.successMessage}">
                    <div class="alert alert-success">
                        <span>✅</span>
                        <span>${sessionScope.successMessage}</span>
                        <button class="close-alert" onclick="this.parentElement.remove()">&times;</button>
                    </div>
                    <c:remove var="successMessage" scope="session" />
                </c:if>

                <c:if test="${not empty sessionScope.errorMessage}">
                    <div class="alert alert-error">
                        <span>⚠️</span>
                        <span>${sessionScope.errorMessage}</span>
                        <button class="close-alert" onclick="this.parentElement.remove()">&times;</button>
                    </div>
                    <c:remove var="errorMessage" scope="session" />
                </c:if>

                <!-- Tabs Navigation -->
                <div class="evaluation-tabs">
                    <a href="#pending" class="tab-link active" data-tab="pending">
                        <span>⏳</span> Pending Evaluation
                        <c:if test="${not empty pendingTenders}">
                            <span class="tab-count">${pendingTenders.size()}</span>
                        </c:if>
                    </a>
                    <a href="#in-progress" class="tab-link" data-tab="in-progress">
                        <span>🔄</span> In Progress
                        <c:if test="${not empty inProgressTenders}">
                            <span class="tab-count">${inProgressTenders.size()}</span>
                        </c:if>
                    </a>
                    <a href="#completed" class="tab-link" data-tab="completed">
                        <span>✅</span> Completed
                        <c:if test="${not empty completedTenders}">
                            <span class="tab-count">${completedTenders.size()}</span>
                        </c:if>
                    </a>
                </div>

                <!-- ======================================================== -->
                <!-- PENDING EVALUATIONS TAB                                  -->
                <!-- ======================================================== -->
                <div id="pending" class="tab-content active">
                    <c:choose>
                        <c:when test="${not empty pendingTenders}">
                            <div class="evaluation-cards">
                                <c:forEach var="tender" items="${pendingTenders}">
                                    <div class="evaluation-card">
                                        <div class="card-header">
                                            <div class="tender-info">
                                                <span class="ref-number">${tender.referenceNumber}</span>
                                                <h3>${tender.title}</h3>
                                                <div class="tender-meta">
                                                    <span class="category-badge">${tender.category.displayName}</span>
                                                    <span class="closing-info">
                                                        Closed: ${tender.formattedClosingDate}
                                                    </span>
                                                </div>
                                            </div>
                                            <div class="bid-summary">
                                                <div class="bid-stat">
                                                    <span class="stat-value">${tender.bidCount}</span>
                                                    <span class="stat-label">Bids Received</span>
                                                </div>
                                            </div>
                                        </div>

                                        <div class="card-footer">
                                            <a href="${pageContext.request.contextPath}/officer/tender-detail?id=${tender.tenderId}" 
                                               class="btn btn-outline">View Details</a>

                                            <c:if test="${tender.bidCount > 0}">
                                                <form action="${pageContext.request.contextPath}/officer/tender-detail" method="POST" 
                                                      style="display: inline;">
                                                    <input type="hidden" name="id" value="${tender.tenderId}">
                                                    <input type="hidden" name="action" value="startEvaluation">
                                                    <button type="submit" class="btn btn-primary">
                                                        Start Evaluation
                                                    </button>
                                                    <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                                                </form>
                                            </c:if>
                                        </div>
                                    </div>
                                </c:forEach>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="empty-tab">
                                <div class="empty-icon">📭</div>
                                <p>No tenders pending evaluation</p>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>

                <!-- ======================================================== -->
                <!-- IN PROGRESS EVALUATIONS TAB                              -->
                <!-- ======================================================== -->
                <div id="in-progress" class="tab-content">
                    <c:choose>
                        <c:when test="${not empty inProgressTenders}">
                            <div class="evaluation-cards">
                                <c:forEach var="tender" items="${inProgressTenders}">
                                    <div class="evaluation-card">
                                        <div class="card-header">
                                            <div class="tender-info">
                                                <span class="ref-number">${tender.referenceNumber}</span>
                                                <h3>${tender.title}</h3>
                                                <div class="tender-meta">
                                                    <span class="category-badge">${tender.category.displayName}</span>
                                                </div>
                                            </div>
                                            <div class="progress-info">
                                                <div class="progress-stats">
                                                    <span class="progress-text">
                                                        ${tender.scoresSubmitted} / ${tender.expectedScores} scores
                                                    </span>
                                                    <div class="progress-bar">
                                                        <div class="progress-fill" style="width: ${tender.progressPercent}%"></div>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>

                                        <div class="evaluator-list">
                                            <h4>Evaluators Progress</h4>
                                            <c:if test="${not empty tender.evaluatorProgressList}">
                                                <c:forEach var="evaluator" items="${tender.evaluatorProgressList}">
                                                    <div class="evaluator-item">
                                                        <span class="evaluator-name">${evaluator.name}</span>
                                                        <span class="evaluator-status ${evaluator.completed ? 'completed' : 'pending'}">
                                                            <c:choose>
                                                                <c:when test="${evaluator.completed}">✅ Complete</c:when>
                                                                <c:otherwise>⏳ ${evaluator.scoresSubmitted}/${evaluator.totalBids} bids</c:otherwise>
                                                            </c:choose>
                                                        </span>
                                                    </div>
                                                </c:forEach>
                                            </c:if>
                                            <c:if test="${empty tender.evaluatorProgressList}">
                                                <div class="evaluator-item">
                                                    <span class="evaluator-name">No evaluator data available</span>
                                                </div>
                                            </c:if>
                                        </div>

                                        <div class="card-footer">
                                            <!-- FIXED: Changed from evaluation-panel to start-evaluation -->
                                            <a href="${pageContext.request.contextPath}/officer/start-evaluation?id=${tender.tenderId}" 
                                               class="btn btn-primary">
                                                Continue Evaluation
                                            </a>
                                        </div>
                                    </div>
                                </c:forEach>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="empty-tab">
                                <div class="empty-icon">📭</div>
                                <p>No evaluations in progress</p>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>

                <!-- ======================================================== -->
                <!-- COMPLETED EVALUATIONS TAB                                -->
                <!-- ======================================================== -->
                <div id="completed" class="tab-content">
                    <c:choose>
                        <c:when test="${not empty completedTenders}">
                            <div class="evaluation-cards">
                                <c:forEach var="tender" items="${completedTenders}">
                                    <div class="evaluation-card completed">
                                        <div class="card-header">
                                            <div class="tender-info">
                                                <span class="ref-number">${tender.referenceNumber}</span>
                                                <h3>${tender.title}</h3>
                                                <div class="tender-meta">
                                                    <span class="category-badge">${tender.category.displayName}</span>
                                                    <span class="status-badge status-${tender.status.name().toLowerCase()}">
                                                        ${tender.status.displayName}
                                                    </span>
                                                </div>
                                            </div>
                                            <div class="completion-badge">
                                                ✅ Evaluation Complete
                                            </div>
                                        </div>

                                        <!-- Ranked Results Summary -->
                                        <c:if test="${not empty tender.rankedBids}">
                                            <div class="ranked-results">
                                                <h4>Top Ranked Bids</h4>
                                                <table class="mini-rank-table">
                                                    <thead>
                                                        <tr>
                                                            <th>Rank</th>
                                                            <th>Supplier</th>
                                                            <th>Bid Amount</th>
                                                            <th>Score</th>
                                                        </tr>
                                                    </thead>
                                                    <tbody>
                                                        <c:forEach var="bid" items="${tender.rankedBids}" begin="0" end="2">
                                                            <tr class="rank-${bid.rank}">
                                                                <td>
                                                                    <c:choose>
                                                                        <c:when test="${bid.rank == 1}">🥇</c:when>
                                                                        <c:when test="${bid.rank == 2}">🥈</c:when>
                                                                        <c:when test="${bid.rank == 3}">🥉</c:when>
                                                                        <c:otherwise>#${bid.rank}</c:otherwise>
                                                                    </c:choose>
                                                                </td>
                                                                <td>${bid.supplierName}</td>
                                                                <td>${bid.formattedBidAmount}</td>
                                                                <td>${bid.formattedFinalScore}</td>
                                                            </tr>
                                                        </c:forEach>
                                                    </tbody>
                                                </table>
                                            </div>
                                        </c:if>

                                        <div class="card-footer">
                                            <a href="${pageContext.request.contextPath}/officer/evaluation-results?id=${tender.tenderId}" 
                                               class="btn btn-outline">View Full Results</a>

                                            <c:if test="${tender.status == 'EVALUATED'}">
                                                <a href="${pageContext.request.contextPath}/officer/award-tender?id=${tender.tenderId}" 
                                                   class="btn btn-primary">
                                                    🏆 Award Contract
                                                </a>
                                            </c:if>

                                            <c:if test="${tender.status == 'AWARDED'}">
                                                <a href="${pageContext.request.contextPath}/officer/award-notice?id=${tender.tenderId}" 
                                                   class="btn btn-success">
                                                    📄 View Award Notice
                                                </a>
                                            </c:if>
                                        </div>
                                    </div>
                                </c:forEach>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="empty-tab">
                                <div class="empty-icon">📭</div>
                                <p>No completed evaluations</p>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>

            </main>
        </div>

        <jsp:include page="/shared/footer.jsp" />

        <script>
            // Tab switching functionality
            document.querySelectorAll('.tab-link').forEach(tab => {
                tab.addEventListener('click', function (e) {
                    e.preventDefault();

                    // Remove active class from all tabs
                    document.querySelectorAll('.tab-link').forEach(t => t.classList.remove('active'));
                    document.querySelectorAll('.tab-content').forEach(c => c.classList.remove('active'));

                    // Add active class to clicked tab
                    this.classList.add('active');

                    // Show corresponding content
                    const tabId = this.getAttribute('data-tab');
                    document.getElementById(tabId).classList.add('active');
                });
            });

            // Check URL hash for tab
            if (window.location.hash) {
                const hash = window.location.hash.substring(1);
                const tab = document.querySelector(`.tab-link[data-tab="${hash}"]`);
                if (tab) {
                    tab.click();
                }
            }

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