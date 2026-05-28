<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Score Bid - ${tender.referenceNumber}</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/ministry-style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/officerdashboard.css">
    <style>
        .card {
            background: white;
            border-radius: 8px;
            padding: 20px;
            margin-bottom: 20px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }
        .info-grid {
            display: grid;
            grid-template-columns: repeat(2, 1fr);
            gap: 15px;
        }
        .info-item label {
            font-weight: bold;
            display: block;
            margin-bottom: 5px;
        }
        .form-group {
            margin-bottom: 20px;
        }
        .form-group label {
            font-weight: bold;
            display: block;
            margin-bottom: 8px;
        }
        .form-group input {
            width: 200px;
            padding: 8px;
            border: 1px solid #ddd;
            border-radius: 4px;
        }
        .btn {
            padding: 10px 20px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            text-decoration: none;
            display: inline-block;
        }
        .btn-primary {
            background-color: #00529B;
            color: white;
        }
        .btn-secondary {
            background-color: #6c757d;
            color: white;
        }
        .score-display {
            font-size: 24px;
            font-weight: bold;
            color: #00529B;
        }
    </style>
</head>
<body>

    <jsp:include page="/shared/navigation.jsp" />

    <div class="dashboard-layout">
        
        <jsp:include page="/shared/sidebar.jsp" />

        <main class="dashboard-main">
            
            <div class="page-header">
                <a href="${pageContext.request.contextPath}/evaluator/score-bids?tenderId=${tender.tenderId}" class="back-link">← Back to Bids</a>
                <h1>Score Bid</h1>
                <p>Tender: <strong>${tender.referenceNumber}</strong> - ${tender.title}</p>
            </div>

            <c:if test="${hasScored}">
                <div class="alert alert-warning">
                    ⚠️ You have already scored this bid. Your scores are shown below.
                </div>
            </c:if>

            <!-- Bid Information -->
            <div class="card">
                <h3>Bid Information</h3>
                <div class="info-grid">
                    <div class="info-item">
                        <label>Supplier:</label>
                        <span>${bid.supplierName}</span>
                    </div>
                    <div class="info-item">
                        <label>Bid Amount:</label>
                        <span>${bid.formattedBidAmount}</span>
                    </div>
                    <div class="info-item">
                        <label>Proposed Timeline:</label>
                        <span>${bid.proposedTimelineDays} days</span>
                    </div>
                    <div class="info-item">
                        <label>Technical Statement:</label>
                        <span>${bid.technicalComplianceStatement}</span>
                    </div>
                </div>
            </div>

            <!-- Automatic Scores -->
            <div class="card">
                <h3>Automatic Scores (System Calculated)</h3>
                <div class="info-grid">
                    <div class="info-item">
                        <label>Lowest Bid Amount:</label>
                        <span>M ${lowestBid}</span>
                    </div>
                    <div class="info-item">
                        <label>Price Score (40% weight):</label>
                        <span>${priceScore}% × 0.40 = <strong>${priceScore * 0.4}%</strong></span>
                    </div>
                    <div class="info-item">
                        <label>Shortest Timeline:</label>
                        <span>${shortestTimeline} days</span>
                    </div>
                    <div class="info-item">
                        <label>Timeline Score (25% weight):</label>
                        <span>${timelineScore}% × 0.25 = <strong>${timelineScore * 0.25}%</strong></span>
                    </div>
                </div>
            </div>

            <!-- Score Form -->
            <div class="card">
                <h3>Enter Technical Score</h3>
                
                <form action="${pageContext.request.contextPath}/evaluator/score-bids" method="POST">
                    <input type="hidden" name="action" value="submitScore">
                    <input type="hidden" name="tenderId" value="${tender.tenderId}">
                    <input type="hidden" name="bidId" value="${bid.bidId}">
                    
                    <div class="form-group">
                        <label for="technicalScore">Technical Compliance Score (0-100):</label>
                        <input type="number" id="technicalScore" name="technicalScore" 
                               min="0" max="100" step="1" required
                               value="${existingScore.technicalComplianceScore}"
                               ${hasScored ? 'readonly disabled' : ''}>
                        <small>Rate the technical compliance of this bid from 0 to 100</small>
                    </div>
                    
                    <c:if test="${hasScored}">
                        <div class="form-group">
                            <label>Your Weighted Total Score:</label>
                            <div class="score-display">
                                ${existingScore.weightedTotal}%
                            </div>
                        </div>
                    </c:if>
                    
                    <div class="form-actions">
                        <c:if test="${not hasScored}">
                            <button type="submit" class="btn btn-primary">Submit Scores</button>
                        </c:if>
                        <a href="${pageContext.request.contextPath}/evaluator/score-bids?tenderId=${tender.tenderId}" 
                           class="btn btn-secondary">Cancel</a>
                    </div>
                </form>
            </div>

        </main>
    </div>

    <jsp:include page="/shared/footer.jsp" />

</body>
</html>