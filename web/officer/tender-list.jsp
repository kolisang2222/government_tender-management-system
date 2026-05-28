<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Manage Tenders - ProcureGov</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/ministry-style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/dashboard.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/tender-list.css">
    <link rel="stylesheet" href="procgov-dashboard.css">
</head>
<body>

    <jsp:include page="/shared/navigation.jsp" />

    <div class="dashboard-layout">
        
        <jsp:include page="/views/shared/sidebar.jsp" />

        <main class="dashboard-main">
            
            <div class="page-header">
                <h1>📋 Manage Tenders</h1>
                <p>View, filter, and manage all government tenders</p>
                <a href="${pageContext.request.contextPath}/officer/create-tender.jsp" class="btn btn-primary">
                    ➕ Create New Tender
                </a>
            </div>

            <!-- Success/Error Messages -->
            <c:if test="${not empty successMessage}">
                <div class="alert alert-success">✅ ${successMessage}</div>
            </c:if>
            <c:if test="${not empty errorMessage}">
                <div class="alert alert-error">⚠️ ${errorMessage}</div>
            </c:if>

            <!-- Filter Section -->
            <div class="filter-section">
                <form action="${pageContext.request.contextPath}/officer/tenders.jsp" method="GET" class="filter-form">
                    <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                    <div class="filter-row">
                        <div class="filter-group">
                            <label for="statusFilter">Status</label>
                            <select id="statusFilter" name="status">
                                <option value="">All Statuses</option>
                                <option value="DRAFT" ${param.status == 'DRAFT' ? 'selected' : ''}>Draft</option>
                                <option value="OPEN" ${param.status == 'OPEN' ? 'selected' : ''}>Open</option>
                                <option value="CLOSED" ${param.status == 'CLOSED' ? 'selected' : ''}>Closed</option>
                                <option value="UNDER_EVALUATION" ${param.status == 'UNDER_EVALUATION' ? 'selected' : ''}>Under Evaluation</option>
                                <option value="EVALUATED" ${param.status == 'EVALUATED' ? 'selected' : ''}>Evaluated</option>
                                <option value="AWARDED" ${param.status == 'AWARDED' ? 'selected' : ''}>Awarded</option>
                            </select>
                        </div>
                        
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
                        
                        <div class="filter-actions">
                            <button type="submit" class="btn btn-primary">🔍 Filter</button>
                            <a href="${pageContext.request.contextPath}/officer/tenders" class="btn btn-secondary">Clear</a>
                        </div>
                    </div>
                </form>
            </div>

            <!-- Tenders Table -->
            <div class="table-container">
                <c:choose>
                    <c:when test="${not empty tenderList}">
                        <table class="tender-table">
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
                                <c:forEach var="tender" items="${tenderList}">
                                    <tr class="status-${tender.status.name().toLowerCase()}">
                                        <td>
                                            <span class="ref-number">${tender.referenceNumber}</span>
                                        </td>
                                        <td>
                                            <span class="tender-title">${tender.title}</span>
                                        </td>
                                        <td>
                                            <span class="category-badge">${tender.category.displayName}</span>
                                        </td>
                                        <td>
                                            <span class="status-badge status-${tender.status.name().toLowerCase()}">
                                                ${tender.status.displayName}
                                            </span>
                                        </td>
                                        <td>
                                            <fmt:formatDate value="${tender.closingDateTime}" 
                                                            pattern="dd MMM yyyy, HH:mm"/>
                                        </td>
                                        <td>
                                            <span class="bid-count">${tender.bidCount} bids</span>
                                        </td>
                                        <td class="actions-cell">
                                            <a href="${pageContext.request.contextPath}/officer/tender-detail?id=${tender.tenderId}" 
                                               class="btn-icon" title="View Details">👁️</a>
                                            
                                            <c:if test="${tender.status == 'DRAFT'}">
                                                <a href="${pageContext.request.contextPath}/officer/edit-tender?id=${tender.tenderId}" 
                                                   class="btn-icon" title="Edit">✏️</a>
                                            </c:if>
                                            
                                            <c:if test="${tender.status == 'DRAFT'}">
                                                <a href="${pageContext.request.contextPath}/officer/publish-tender?id=${tender.tenderId}" 
                                                   class="btn-icon btn-publish" title="Publish">📢</a>
                                            </c:if>
                                            
                                            <c:if test="${tender.status == 'CLOSED'}">
                                                <a href="${pageContext.request.contextPath}/officer/start-evaluation?id=${tender.tenderId}" 
                                                   class="btn-icon btn-evaluate" title="Start Evaluation">⭐</a>
                                            </c:if>
                                            
                                            <c:if test="${tender.status == 'EVALUATED'}">
                                                <a href="${pageContext.request.contextPath}/officer/award-tender?id=${tender.tenderId}" 
                                                   class="btn-icon btn-award" title="Award Contract">🏆</a>
                                            </c:if>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </c:when>
                    <c:otherwise>
                        <div class="empty-state">
                            <p>📭 No tenders found</p>
                            <a href="${pageContext.request.contextPath}/officer/create-tender.jsp" class="btn btn-primary">
                                Create Your First Tender
                            </a>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>

            <!-- Pagination -->
            <c:if test="${totalPages > 1}">
                <div class="pagination">
                    <c:if test="${currentPage > 1}">
                        <a href="${pageContext.request.contextPath}/officer/tenders?page=${currentPage - 1}&status=${param.status}&category=${param.category}&keyword=${param.keyword}" 
                           class="page-link">← Previous</a>
                    </c:if>
                    
                    <c:forEach begin="1" end="${totalPages}" var="page">
                        <a href="${pageContext.request.contextPath}/officer/tenders?page=${page}&status=${param.status}&category=${param.category}&keyword=${param.keyword}" 
                           class="page-link ${page == currentPage ? 'active' : ''}">${page}</a>
                    </c:forEach>
                    
                    <c:if test="${currentPage < totalPages}">
                        <a href="${pageContext.request.contextPath}/officer/tenders?page=${currentPage + 1}&status=${param.status}&category=${param.category}&keyword=${param.keyword}" 
                           class="page-link">Next →</a>
                    </c:if>
                </div>
            </c:if>

        </main>
    </div>

    <jsp:include page="/shared/footer.jsp" />

</body>
</html>