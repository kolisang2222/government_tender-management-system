<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Tender Details - ProcureGov</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/ministry-style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/supplierdashboard.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/tender-detail.css">
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
                    <a href="${pageContext.request.contextPath}/supplier/tenders" class="back-link">
                        Back to Tenders
                    </a>
                    <h1>Tender Details</h1>
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

            <c:if test="${not empty tender}">
                
                <!-- Bid Status Banner (if already submitted) -->
                <c:if test="${hasBid}">
                    <div class="bid-status-banner">
                        <div class="banner-icon">OK</div>
                        <div class="banner-content">
                            <strong>You have already submitted a bid for this tender.</strong>
                            <p>Your bid is currently under review. You cannot submit another bid.</p>
                        </div>
                        <a href="${pageContext.request.contextPath}/supplier/bids" class="btn btn-outline-light">
                            View My Bid
                        </a>
                    </div>
                </c:if>

                <!-- Tender Header -->
                <div class="tender-header-card">
                    <div class="header-top">
                        <span class="ref-number">${tender.referenceNumber}</span>
                        <span class="status-badge status-${tender.status.name().toLowerCase()}">
                            ${tender.status.displayName}
                        </span>
                    </div>
                    <h2>${tender.title}</h2>
                    <div class="header-meta">
                        <span class="category-badge">${tender.category.displayName}</span>
                        <span class="estimated-value">Estimated Value: ${tender.formattedEstimatedValue}</span>
                    </div>
                </div>

                <!-- Action Bar -->
                <div class="action-bar">
                    <div class="deadline-info">
                        <span class="deadline-label">Closing Date:</span>
                        <span class="deadline-value">${tender.formattedClosingDateTime}</span>
                        <span class="time-remaining ${tender.urgent ? 'urgent' : ''}">${tender.timeRemaining}</span>
                    </div>
                    
                    <div class="action-buttons">
                        <c:choose>
                            <c:when test="${hasBid}">
                                <a href="${pageContext.request.contextPath}/supplier/bids" class="btn btn-secondary">
                                    View My Bid
                                </a>
                            </c:when>
                            <c:when test="${tender.canSubmitBid}">
                                <a href="${pageContext.request.contextPath}/supplier/submit-bid?id=${tender.tenderId}" 
                                   class="btn btn-primary btn-lg">
                                    Submit Bid
                                </a>
                            </c:when>
                            <c:otherwise>
                                <button class="btn btn-secondary" disabled>
                                    Bidding Closed
                                </button>
                            </c:otherwise>
                        </c:choose>
                        
                        <a href="${pageContext.request.contextPath}/supplier/download-tender?id=${tender.tenderId}" 
                           class="btn btn-outline">
                            Download Tender Notice
                        </a>
                    </div>
                </div>

                <!-- Tender Details Grid -->
                <div class="details-grid">
                    
                    <!-- Left Column -->
                    <div class="details-column">
                        
                        <!-- Description Card -->
                        <div class="detail-card">
                            <h3>Description</h3>
                            <div class="description-content">
                                ${tender.description}
                            </div>
                        </div>
                        
                        <!-- Requirements Card -->
                        <div class="detail-card">
                            <h3>Requirements</h3>
                            <ul class="requirements-list">
                                <li>Valid Lesotho business registration certificate</li>
                                <li>Current tax clearance certificate from LRA</li>
                                <li>Minimum 5 years experience in relevant field</li>
                                <li>Proof of similar projects completed</li>
                                <li>Valid professional licenses and certifications</li>
                            </ul>
                        </div>
                        
                    </div>
                    
                    <!-- Right Column -->
                    <div class="details-column">
                        
                        <!-- Key Information Card -->
                        <div class="detail-card">
                            <h3>Key Information</h3>
                            <table class="info-table">
                                <tr>
                                    <td class="info-label">Reference Number</td>
                                    <td class="info-value">${tender.referenceNumber}</td>
                                </tr>
                                <tr>
                                    <td class="info-label">Category</td>
                                    <td class="info-value">${tender.category.displayName}</td>
                                </tr>
                                <tr>
                                    <td class="info-label">Estimated Value</td>
                                    <td class="info-value">${tender.formattedEstimatedValue}</td>
                                </tr>
                                <tr>
                                    <td class="info-label">Closing Date</td>
                                    <td class="info-value">${tender.formattedClosingDateTime}</td>
                                </tr>
                                <tr>
                                    <td class="info-label">Time Remaining</td>
                                    <td class="info-value ${tender.urgent ? 'urgent' : ''}">${tender.timeRemaining}</td>
                                </tr>
                                <tr>
                                    <td class="info-label">Published Date</td>
                                    <td class="info-value">${tender.formattedCreatedAt}</td>
                                </tr>
                                <tr>
                                    <td class="info-label">Procurement Officer</td>
                                    <td class="info-value">${tender.createdByName}</td>
                                </tr>
                            </table>
                        </div>
                        
                        <!-- Important Dates Card -->
                        <div class="detail-card">
                            <h3>Important Dates</h3>
                            <div class="timeline">
                                <div class="timeline-item">
                                    <div class="timeline-marker published"></div>
                                    <div class="timeline-content">
                                        <span class="timeline-title">Published</span>
                                        <span class="timeline-date">${tender.formattedCreatedAt}</span>
                                    </div>
                                </div>
                                <div class="timeline-item">
                                    <div class="timeline-marker closing"></div>
                                    <div class="timeline-content">
                                        <span class="timeline-title">Closing Date</span>
                                        <span class="timeline-date">${tender.formattedClosingDateTime}</span>
                                    </div>
                                </div>
                                <c:if test="${tender.status == 'AWARDED'}">
                                    <div class="timeline-item">
                                        <div class="timeline-marker awarded"></div>
                                        <div class="timeline-content">
                                            <span class="timeline-title">Awarded</span>
                                            <span class="timeline-date">${tender.formattedAwardDate}</span>
                                        </div>
                                    </div>
                                </c:if>
                            </div>
                        </div>
                        
                        <!-- Contact Information Card -->
                        <div class="detail-card">
                            <h3>Contact Information</h3>
                            <div class="contact-info">
                                <div class="contact-item">
                                    <span class="contact-label">Department:</span>
                                    <span class="contact-value">Procurement Office</span>
                                </div>
                                <div class="contact-item">
                                    <span class="contact-label">Ministry:</span>
                                    <span class="contact-value">Ministry of Public Works</span>
                                </div>
                                <div class="contact-item">
                                    <span class="contact-label">Phone:</span>
                                    <span class="contact-value">+266 2232 1000</span>
                                </div>
                                <div class="contact-item">
                                    <span class="contact-label">Email:</span>
                                    <span class="contact-value">procurement@mpw.gov.ls</span>
                                </div>
                                <div class="contact-item">
                                    <span class="contact-label">Address:</span>
                                    <span class="contact-value">P.O. Box 20, Maseru 100, Lesotho</span>
                                </div>
                            </div>
                        </div>
                        
                    </div>
                </div>

                <!-- Award Information (if awarded) -->
                <c:if test="${tender.status == 'AWARDED' && not empty tender.winningSupplierName}">
                    <div class="award-card">
                        <h3>Award Information</h3>
                        <div class="award-details">
                            <div class="award-item">
                                <span class="award-label">Winning Supplier:</span>
                                <span class="award-value highlight">${tender.winningSupplierName}</span>
                            </div>
                            <div class="award-item">
                                <span class="award-label">Awarded Amount:</span>
                                <span class="award-value amount">${tender.formattedAwardedValue}</span>
                            </div>
                            <div class="award-item">
                                <span class="award-label">Award Date:</span>
                                <span class="award-value">${tender.formattedAwardDate}</span>
                            </div>
                            <c:if test="${not empty tender.awardJustification}">
                                <div class="award-justification">
                                    <span class="award-label">Justification:</span>
                                    <p>${tender.awardJustification}</p>
                                </div>
                            </c:if>
                        </div>
                    </div>
                </c:if>

            </c:if>
            
            <c:if test="${empty tender}">
                <div class="error-card">
                    <div class="error-icon">!</div>
                    <h3>Tender Not Found</h3>
                    <p>The tender you are looking for could not be found.</p>
                    <a href="${pageContext.request.contextPath}/supplier/tenders" class="btn btn-primary">
                        Browse Tenders
                    </a>
                </div>
            </c:if>

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