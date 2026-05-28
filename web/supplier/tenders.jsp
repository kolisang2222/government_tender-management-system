<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Browse Tenders - ProcureGov</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/ministry-style.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/dashboard.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/supplier-tenders.css">
        <link rel="stylesheet" href="procgov-dashboard.css">
    </head>
    <body>

        <jsp:include page="/shared/navigation.jsp" />

        <div class="dashboard-layout">

            <jsp:include page="/shared/sidebar.jsp" />

            <main class="dashboard-main">

                <div class="page-header">
                    <div class="header-left">
                        <h1>Browse Tenders</h1>
                        <p>View and bid on open government tenders</p>
                    </div>
                </div>

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

                <div class="stats-summary">
                    <div class="stat-card">
                        <div class="stat-icon">O</div>
                        <div class="stat-content">
                            <span class="stat-value">${openTendersCount}</span>
                            <span class="stat-label">Open Tenders</span>
                        </div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-icon">C</div>
                        <div class="stat-content">
                            <span class="stat-value">${closingSoonCount}</span>
                            <span class="stat-label">Closing Soon</span>
                        </div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-icon">B</div>
                        <div class="stat-content">
                            <span class="stat-value">${myBidsCount}</span>
                            <span class="stat-label">My Active Bids</span>
                        </div>
                    </div>
                </div>

                <div class="filter-section">
                    <form action="${pageContext.request.contextPath}/supplier/tenders" method="GET" class="filter-form">
                        <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                        <div class="filter-row">
                            <div class="filter-group">
                                <label for="categoryFilter">Category</label>
                                <select id="categoryFilter" name="category">
                                    <option value="">All Categories</option>
                                    <option value="CONSTRUCTION" ${param.category == 'CONSTRUCTION' ? 'selected' : ''}>Construction</option>
                                    <option value="ROADS" ${param.category == 'ROADS' ? 'selected' : ''}>Roads</option>
                                    <option value="ELECTRICAL" ${param.category == 'ELECTRICAL' ? 'selected' : ''}>Electrical</option>
                                    <option value="PLUMBING" ${param.category == 'PLUMBING' ? 'selected' : ''}>Plumbing</option>
                                    <option value="GENERAL_SERVICES" ${param.category == 'GENERAL_SERVICES' ? 'selected' : ''}>General Services</option>
                                </select>
                            </div>
                            <div class="filter-group">
                                <label for="searchKeyword">Search</label>
                                <input type="text" id="searchKeyword" name="keyword" 
                                       placeholder="Reference or title..." value="${param.keyword}">
                            </div>
                            <div class="filter-group">
                                <label for="sortBy">Sort By</label>
                                <select id="sortBy" name="sort">
                                    <option value="newest" ${param.sort == 'newest' ? 'selected' : ''}>Newest First</option>
                                    <option value="closing" ${param.sort == 'closing' ? 'selected' : ''}>Closing Soon</option>
                                    <option value="value_high" ${param.sort == 'value_high' ? 'selected' : ''}>Value (High to Low)</option>
                                    <option value="value_low" ${param.sort == 'value_low' ? 'selected' : ''}>Value (Low to High)</option>
                                </select>
                            </div>
                            <div class="filter-actions">
                                <button type="submit" class="btn btn-primary">Filter</button>
                                <a href="${pageContext.request.contextPath}/supplier/tenders" class="btn btn-secondary">Clear</a>
                            </div>
                        </div>
                    </form>
                </div>

                <div class="tenders-grid">
                    <c:choose>
                        <c:when test="${not empty tenders}">
                            <c:forEach var="tender" items="${tenders}">
                                <c:set var="hasBid" value="${tender.hasBid()}" />
                                <div class="tender-card ${hasBid ? 'has-bid' : ''}">
                                    <div class="card-header">
                                        <div class="tender-category">
                                            <span class="category-badge">${tender.category.displayName}</span>
                                        </div>
                                        <c:if test="${hasBid}">
                                            <span class="bid-badge">Bid Submitted</span>
                                        </c:if>
                                    </div>

                                    <div class="card-body">
                                        <span class="ref-number">${tender.referenceNumber}</span>
                                        <h3>${tender.title}</h3>
                                        <p class="tender-description">${tender.description}</p>

                                        <div class="tender-details">
                                            <div class="detail-item">
                                                <span class="detail-label">Estimated Value</span>
                                                <span class="detail-value">${tender.formattedEstimatedValue}</span>
                                            </div>
                                            <div class="detail-item">
                                                <span class="detail-label">Closing Date</span>
                                                <span class="detail-value">${tender.formattedClosingDateTime}</span>
                                            </div>
                                            <div class="detail-item">
                                                <span class="detail-label">Time Remaining</span>
                                                <span class="detail-value ${tender.urgent ? 'urgent' : ''}">${tender.timeRemaining}</span>
                                            </div>
                                        </div>
                                    </div>

                                    <div class="card-footer">
                                        <a href="${pageContext.request.contextPath}/supplier/tender-detail?id=${tender.tenderId}" 
                                           class="btn btn-outline">
                                            View Details
                                        </a>

                                        <c:choose>
                                            <c:when test="${hasBid}">
                                                <a href="${pageContext.request.contextPath}/supplier/my-bids" 
                                                   class="btn btn-secondary">
                                                    View My Bid
                                                </a>
                                            </c:when>
                                            <c:otherwise>
                                                <a href="${pageContext.request.contextPath}/supplier/submit-bid?id=${tender.tenderId}" 
                                                   class="btn btn-primary">
                                                    Submit Bid
                                                </a>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                </div>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <div class="empty-state">
                                <div class="empty-icon">!</div>
                                <h3>No Tenders Found</h3>
                                <p>There are no open tenders matching your criteria.</p>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>

                <c:if test="${totalPages > 1}">
                    <div class="pagination">
                        <c:if test="${currentPage > 1}">
                            <a href="${pageContext.request.contextPath}/supplier/tenders?page=${currentPage - 1}&category=${param.category}&keyword=${param.keyword}&sort=${param.sort}" 
                               class="page-link">Previous</a>
                        </c:if>

                        <c:forEach begin="1" end="${totalPages}" var="page">
                            <c:choose>
                                <c:when test="${page == currentPage}">
                                    <span class="page-link active">${page}</span>
                                </c:when>
                                <c:otherwise>
                                    <a href="${pageContext.request.contextPath}/supplier/tenders?page=${page}&category=${param.category}&keyword=${param.keyword}&sort=${param.sort}" 
                                       class="page-link">${page}</a>
                                </c:otherwise>
                            </c:choose>
                        </c:forEach>

                        <c:if test="${currentPage < totalPages}">
                            <a href="${pageContext.request.contextPath}/supplier/tenders?page=${currentPage + 1}&category=${param.category}&keyword=${param.keyword}&sort=${param.sort}" 
                               class="page-link">Next</a>
                        </c:if>
                    </div>
                </c:if>

            </main>
        </div>

        <jsp:include page="/shared/footer.jsp" />

        <script>
            document.getElementById('categoryFilter').addEventListener('change', function () {
                this.form.submit();
            });

            document.getElementById('sortBy').addEventListener('change', function () {
                this.form.submit();
            });

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