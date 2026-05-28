<%-- 
    Document   : awards
    Created on : Apr 15, 2026, 1:32:29 PM
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
    <title>Award Contracts - ProcureGov</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/ministry-style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/officerdashboard.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/awards.css">
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
                    <h1>🏆 Award Contracts</h1>
                    <p>Review evaluated tenders and award contracts to winning suppliers</p>
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
            <div class="award-tabs">
                <a href="#pending-award" class="tab-link active" data-tab="pending-award">
                    <span>⏳</span> Pending Award
                    <c:if test="${not empty pendingAwardTenders}">
                        <span class="tab-count">${pendingAwardTenders.size()}</span>
                    </c:if>
                </a>
                <a href="#awarded" class="tab-link" data-tab="awarded">
                    <span>✅</span> Awarded Contracts
                    <c:if test="${not empty awardedTenders}">
                        <span class="tab-count">${awardedTenders.size()}</span>
                    </c:if>
                </a>
            </div>

            <!-- ======================================================== -->
            <!-- PENDING AWARD TAB                                         -->
            <!-- ======================================================== -->
            <div id="pending-award" class="tab-content active">
                <c:choose>
                    <c:when test="${not empty pendingAwardTenders}">
                        <div class="award-cards">
                            <c:forEach var="tender" items="${pendingAwardTenders}">
                                <div class="award-card">
                                    <div class="card-header">
                                        <div class="tender-info">
                                            <span class="ref-number">${tender.referenceNumber}</span>
                                            <h3>${tender.title}</h3>
                                            <div class="tender-meta">
                                                <span class="category-badge">${tender.category.displayName}</span>
                                                <span class="status-badge status-evaluated">✅ Evaluated</span>
                                            </div>
                                        </div>
                                        <div class="evaluation-summary">
                                            <div class="stat-item">
                                                <span class="stat-value">${tender.bidCount}</span>
                                                <span class="stat-label">Bids</span>
                                            </div>
                                        </div>
                                    </div>
                                    
                                    <!-- Top Ranked Bids -->
                                    <c:if test="${not empty tender.rankedBids}">
                                        <div class="ranked-section">
                                            <h4>📊 Evaluation Results - Top Ranked Bids</h4>
                                            <table class="ranked-table">
                                                <thead>
                                                    <tr>
                                                        <th>Rank</th>
                                                        <th>Supplier</th>
                                                        <th>Bid Amount</th>
                                                        <th>Timeline</th>
                                                        <th>Final Score</th>
                                                        <th>Action</th>
                                                    </tr>
                                                </thead>
                                                <tbody>
                                                    <c:forEach var="bid" items="${tender.rankedBids}" begin="0" end="4">
                                                        <tr class="rank-${bid.rank}">
                                                            <td>
                                                                <c:choose>
                                                                    <c:when test="${bid.rank == 1}">
                                                                        <span class="rank-badge gold">🥇 1st</span>
                                                                    </c:when>
                                                                    <c:when test="${bid.rank == 2}">
                                                                        <span class="rank-badge silver">🥈 2nd</span>
                                                                    </c:when>
                                                                    <c:when test="${bid.rank == 3}">
                                                                        <span class="rank-badge bronze">🥉 3rd</span>
                                                                    </c:when>
                                                                    <c:otherwise>
                                                                        <span class="rank-badge">#${bid.rank}</span>
                                                                    </c:otherwise>
                                                                </c:choose>
                                                            </td>
                                                            <td>
                                                                <span class="supplier-name">${bid.supplierName}</span>
                                                            </td>
                                                            <td>
                                                                <span class="bid-amount">${bid.formattedBidAmount}</span>
                                                            </td>
                                                            <td>
                                                                <span class="timeline">${bid.proposedTimelineDays} days</span>
                                                            </td>
                                                            <td>
                                                                <span class="final-score">
                                                                    <c:choose>
                                                                        <c:when test="${bid.rank == 1}">
                                                                            <strong>${bid.formattedFinalScore}</strong>
                                                                        </c:when>
                                                                        <c:otherwise>
                                                                            ${bid.formattedFinalScore}
                                                                        </c:otherwise>
                                                                    </c:choose>
                                                                </span>
                                                            </td>
                                                            <td>
                                                                <c:if test="${bid.rank == 1}">
                                                                    <button type="button" 
                                                                            class="btn btn-sm btn-success"
                                                                            onclick="showAwardModal(${tender.tenderId}, ${bid.bidId}, '${bid.supplierName}', '${bid.formattedBidAmount}', '${tender.referenceNumber}')">
                                                                        🏆 Award Contract
                                                                    </button>
                                                                </c:if>
                                                            </td>
                                                        </tr>
                                                    </c:forEach>
                                                </tbody>
                                            </table>
                                        </div>
                                    </c:if>
                                    
                                    <div class="card-footer">
                                        <a href="${pageContext.request.contextPath}/officer/evaluation-results?id=${tender.tenderId}" 
                                           class="btn btn-outline">
                                            View Full Results
                                        </a>
                                        
                                        <c:if test="${empty tender.rankedBids}">
                                            <span class="no-bids-message">No bids available for award</span>
                                        </c:if>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="empty-tab">
                            <div class="empty-icon">📭</div>
                            <h3>No Tenders Pending Award</h3>
                            <p>All evaluated tenders have been awarded or no tenders are ready for award.</p>
                            <a href="${pageContext.request.contextPath}/officer/evaluations.jsp" class="btn btn-primary">
                                Go to Evaluations
                            </a>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>

            <!-- ======================================================== -->
            <!-- AWARDED CONTRACTS TAB                                     -->
            <!-- ======================================================== -->
            <div id="awarded" class="tab-content">
                <c:choose>
                    <c:when test="${not empty awardedTenders}">
                        <div class="awarded-cards">
                            <c:forEach var="tender" items="${awardedTenders}">
                                <div class="awarded-card">
                                    <div class="card-header awarded">
                                        <div class="tender-info">
                                            <span class="ref-number">${tender.referenceNumber}</span>
                                            <h3>${tender.title}</h3>
                                            <div class="tender-meta">
                                                <span class="category-badge">${tender.category.displayName}</span>
                                                <span class="status-badge status-awarded">🏆 Awarded</span>
                                            </div>
                                        </div>
                                        <div class="award-date">
                                            Awarded: <fmt:formatDate value="${tender.awardDate}" pattern="dd MMM yyyy"/>
                                        </div>
                                    </div>
                                    
                                    <!-- Award Details -->
                                    <div class="award-details">
                                        <div class="award-info-grid">
                                            <div class="award-info-item">
                                                <span class="info-label">Winning Supplier</span>
                                                <span class="info-value highlight">${tender.winningSupplierName}</span>
                                            </div>
                                            <div class="award-info-item">
                                                <span class="info-label">Awarded Amount</span>
                                                <span class="info-value amount">${tender.formattedAwardedValue}</span>
                                            </div>
                                            <div class="award-info-item">
                                                <span class="info-label">Estimated Value</span>
                                                <span class="info-value">${tender.formattedEstimatedValue}</span>
                                            </div>
                                            <div class="award-info-item">
                                                <span class="info-label">Bids Received</span>
                                                <span class="info-value">${tender.bidCount} bids</span>
                                            </div>
                                        </div>
                                        
                                        <c:if test="${not empty tender.awardJustification}">
                                            <div class="award-justification">
                                                <span class="info-label">Award Justification</span>
                                                <p>${tender.awardJustification}</p>
                                            </div>
                                        </c:if>
                                    </div>
                                    
                                    <div class="card-footer">
                                        <a href="${pageContext.request.contextPath}/officer/award-notice?id=${tender.tenderId}" 
                                           class="btn btn-outline">
                                            📄 View Award Notice
                                        </a>
                                        <a href="${pageContext.request.contextPath}/officer/tender-detail?id=${tender.tenderId}" 
                                           class="btn btn-outline">
                                            👁️ View Tender
                                        </a>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="empty-tab">
                            <div class="empty-icon">🏆</div>
                            <h3>No Awarded Contracts</h3>
                            <p>No contracts have been awarded yet.</p>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>

        </main>
    </div>

    <!-- ============================================================ -->
    <!-- AWARD MODAL                                                    -->
    <!-- ============================================================ -->
    <div id="awardModal" class="modal">
        <div class="modal-content">
            <div class="modal-header">
                <h2>🏆 Award Contract</h2>
                <button class="modal-close" onclick="closeAwardModal()">&times;</button>
            </div>
            
            <form action="${pageContext.request.contextPath}/officer/award-tender" method="POST" id="awardForm">
                <input type="hidden" name="tenderId" id="modalTenderId">
                <input type="hidden" name="winningBidId" id="modalBidId">
                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                
                <div class="modal-body">
                    <div class="award-summary">
                        <div class="summary-item">
                            <span class="summary-label">Tender Reference</span>
                            <span class="summary-value" id="modalTenderRef"></span>
                        </div>
                        <div class="summary-item">
                            <span class="summary-label">Winning Supplier</span>
                            <span class="summary-value highlight" id="modalSupplierName"></span>
                        </div>
                        <div class="summary-item">
                            <span class="summary-label">Bid Amount</span>
                            <span class="summary-value amount" id="modalBidAmount"></span>
                        </div>
                    </div>
                    
                    <div class="form-group">
                        <label for="awardValue">
                            Awarded Contract Value (Maloti) <span class="required">*</span>
                        </label>
                        <div class="input-wrapper">
                            <span class="currency-symbol">M</span>
                            <input type="number" 
                                   id="awardValue" 
                                   name="awardValue" 
                                   step="0.01" 
                                   min="0.01"
                                   placeholder="Enter contract value"
                                   required>
                        </div>
                        <small class="field-hint">This may differ from the bid amount if negotiated</small>
                    </div>
                    
                    <div class="form-group">
                        <label for="justification">
                            Award Justification <span class="required">*</span>
                        </label>
                        <textarea id="justification" 
                                  name="justification" 
                                  rows="4"
                                  placeholder="Provide justification for awarding this contract..."
                                  minlength="20"
                                  required></textarea>
                        <small class="field-hint">Minimum 20 characters</small>
                    </div>
                    
                    <div class="warning-box">
                        <span class="warning-icon">⚠️</span>
                        <span>This action will officially award the contract and notify all bidders via email.</span>
                    </div>
                </div>
                
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" onclick="closeAwardModal()">
                        Cancel
                    </button>
                    <button type="submit" class="btn btn-success">
                        🏆 Confirm Award
                    </button>
                </div>
            </form>
        </div>
    </div>

    <jsp:include page="/shared/footer.jsp" />

    <script>
        // Tab switching functionality
        document.querySelectorAll('.tab-link').forEach(tab => {
            tab.addEventListener('click', function(e) {
                e.preventDefault();
                
                document.querySelectorAll('.tab-link').forEach(t => t.classList.remove('active'));
                document.querySelectorAll('.tab-content').forEach(c => c.classList.remove('active'));
                
                this.classList.add('active');
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
        
        // Award Modal Functions
        function showAwardModal(tenderId, bidId, supplierName, bidAmount, tenderRef) {
            document.getElementById('modalTenderId').value = tenderId;
            document.getElementById('modalBidId').value = bidId;
            document.getElementById('modalTenderRef').textContent = tenderRef;
            document.getElementById('modalSupplierName').textContent = supplierName;
            document.getElementById('modalBidAmount').textContent = bidAmount;
            
            // Set default award value to bid amount (remove 'M ' and commas)
            const numericValue = bidAmount.replace(/[^0-9.]/g, '');
            document.getElementById('awardValue').value = numericValue;
            
            document.getElementById('awardModal').style.display = 'flex';
        }
        
        function closeAwardModal() {
            document.getElementById('awardModal').style.display = 'none';
        }
        
        // Close modal when clicking outside
        window.onclick = function(event) {
            const modal = document.getElementById('awardModal');
            if (event.target === modal) {
                closeAwardModal();
            }
        }
        
        // Form validation
        document.getElementById('awardForm').addEventListener('submit', function(e) {
            const awardValue = document.getElementById('awardValue').value;
            const justification = document.getElementById('justification').value.trim();
            
            if (!awardValue || parseFloat(awardValue) <= 0) {
                e.preventDefault();
                alert('Please enter a valid award value greater than zero.');
                return false;
            }
            
            if (justification.length < 20) {
                e.preventDefault();
                alert('Award justification must be at least 20 characters.');
                return false;
            }
            
            return confirm('Are you sure you want to award this contract? This will notify all bidders.');
        });
        
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