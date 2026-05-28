<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Award Contract - ProcureGov</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/ministry-style.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/officerdashboard.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/award-tender.css">
    </head>
    <body>

        <jsp:include page="/shared/navigation.jsp" />

        <div class="dashboard-layout">

            <jsp:include page="/shared/sidebar.jsp" />

            <main class="dashboard-main">

                <div class="page-header">
                    <a href="${pageContext.request.contextPath}/officer/tender-detail?id=${tender.tenderId}" class="back-link">
                        Back to Tender Details
                    </a>
                    <h1>Award Contract</h1>
                    <p>Select the winning bid and enter award details</p>
                </div>

                <c:if test="${not empty errorMessage}">
                    <div class="alert alert-error">
                        <span>!</span>
                        <span>${errorMessage}</span>
                    </div>
                </c:if>

                <c:if test="${not empty tender}">

                    <!-- Tender Summary -->
                    <div class="summary-card">
                        <h2>${tender.title}</h2>
                        <div class="summary-details">
                            <span class="ref-number">${tender.referenceNumber}</span>
                            <span class="category">${tender.category.displayName}</span>
                            <span class="estimated-value">Estimated: ${tender.formattedEstimatedValue}</span>
                        </div>
                    </div>

                    <!-- Ranked Bids -->
                    <div class="bids-section">
                        <h3>Ranked Bids</h3>

                        <c:choose>
                            <c:when test="${not empty rankedBids}">
                                <form action="${pageContext.request.contextPath}/officer/award-tender" method="POST" id="awardForm">
                                    <input type="hidden" name="tenderId" value="${tender.tenderId}">
                                    <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                                    <table class="bids-table">
                                        <thead>
                                            <tr>
                                                <th>Select</th>
                                                <th>Rank</th>
                                                <th>Supplier</th>
                                                <th>Bid Amount</th>
                                                <th>Timeline</th>
                                                <th>Final Score</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach var="bid" items="${rankedBids}">
                                                <tr class="${bid.rank == 1 ? 'top-ranked' : ''}">
                                                    <td>
                                                        <input type="radio" name="winningBidId" value="${bid.bidId}" 
                                                               ${bid.rank == 1 ? 'checked' : ''} required>
                                                    </td>
                                                    <td>
                                                        <c:choose>
                                                            <c:when test="${bid.rank == 1}">1st</c:when>
                                                            <c:when test="${bid.rank == 2}">2nd</c:when>
                                                            <c:when test="${bid.rank == 3}">3rd</c:when>
                                                            <c:otherwise>${bid.rank}th</c:otherwise>
                                                        </c:choose>
                                                    </td>
                                                    <td>${bid.supplierName}</td>
                                                    <td>${bid.formattedBidAmount}</td>
                                                    <td>${bid.proposedTimelineDays} days</td>
                                                    <td>${bid.formattedFinalScore}</td>
                                                </tr>
                                            </c:forEach>
                                        </tbody>
                                    </table>

                                    <!-- Award Details -->
                                    <div class="award-section">
                                        <div class="form-group">
                                            <label for="awardValue">
                                                Awarded Contract Value (Maloti) <span class="required">*</span>
                                            </label>
                                            <div class="input-wrapper">
                                                <span class="currency-symbol">M</span>
                                                <input type="number" id="awardValue" name="awardValue" 
                                                       step="0.01" min="0.01" required>
                                            </div>
                                            <small>This may differ from the bid amount if negotiated</small>
                                        </div>

                                        <div class="form-group">
                                            <label for="justification">
                                                Award Justification <span class="required">*</span>
                                            </label>
                                            <textarea id="justification" name="justification" rows="5" 
                                                      placeholder="Provide justification for awarding this contract..." 
                                                      minlength="20" required></textarea>
                                            <small>Minimum 20 characters</small>
                                        </div>

                                        <div class="warning-box">
                                            <span>!</span>
                                            <span>This action will officially award the contract and notify all bidders via email.</span>
                                        </div>
                                    </div>

                                    <div class="form-actions">
                                        <button type="submit" class="btn btn-primary">Confirm Award</button>
                                        <a href="${pageContext.request.contextPath}/officer/tender-detail?id=${tender.tenderId}" 
                                           class="btn btn-secondary">Cancel</a>
                                    </div>

                                </form>
                            </c:when>
                            <c:otherwise>
                                <p class="empty-state">No ranked bids available.</p>
                            </c:otherwise>
                        </c:choose>
                    </div>

                </c:if>

            </main>
        </div>

        <jsp:include page="/shared/footer.jsp" />

        <script>
            document.getElementById('awardForm').addEventListener('submit', function (e) {
                const awardValue = document.getElementById('awardValue').value;
                const justification = document.getElementById('justification').value.trim();

                if (!awardValue || parseFloat(awardValue) <= 0) {
                    e.preventDefault();
                    alert('Please enter a valid award value greater than zero.');
                    return false;
                }

                if (justification.length < 20) {
                    e.preventDefault();
                    alert('Justification must be at least 20 characters.');
                    return false;
                }

                return confirm('Are you sure you want to award this contract? All bidders will be notified via email.');
            });
        </script>

    </body>
</html>