<%-- 
    Document   : tenders
    Created on : Apr 15, 2026, 1:08:17 PM
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
    <title>Manage Tenders - ProcureGov</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/ministry-style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/officerdashboard.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/tender-list.css">
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
                    <h1>Manage Tenders</h1>
                    <p>View, filter, and manage all government tenders</p>
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

            <c:if test="${not empty errorMessage}">
                <div class="alert alert-error">
                    <span>!</span>
                    <span>${errorMessage}</span>
                    <button class="close-alert" onclick="this.parentElement.remove()">&times;</button>
                </div>
            </c:if>

            <!-- Filter Section -->
            <div class="filter-section">
                <form action="${pageContext.request.contextPath}/officer/tenders" method="GET" class="filter-form">
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
                            <button type="submit" class="btn btn-primary">Filter</button>
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
                                    <tr class="status-row status-${tender.status.name().toLowerCase()}">
                                        <td>
                                            <span class="ref-number">${tender.referenceNumber}</span>
                                        </td>
                                        <td>
                                            <span class="tender-title">${tender.title}</span>
                                        </td>
                                        <td>
                                            <span class="category-badge">
                                                ${tender.category.displayName}
                                            </span>
                                        </td>
                                        <td>
                                            <span class="status-badge status-${tender.status.name().toLowerCase()}">
                                                ${tender.status.displayName}
                                            </span>
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${not empty tender.closingDateTime}">
                                                    ${tender.formattedClosingDateTime}
                                                </c:when>
                                                <c:otherwise>
                                                    N/A
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${tender.bidCount > 0}">
                                                    <a href="${pageContext.request.contextPath}/officer/tender-detail?id=${tender.tenderId}" 
                                                       class="bid-count-link">
                                                        ${tender.bidCount} bid${tender.bidCount > 1 ? 's' : ''}
                                                    </a>
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
                                                   class="btn-icon" title="Edit Tender">Edit</a>
                                            </c:if>
                                            
                                            <c:if test="${tender.status == 'DRAFT'}">
                                                <form action="${pageContext.request.contextPath}/officer/tender-detail" method="POST" 
                                                      style="display: inline;" onsubmit="return confirm('Publish this tender? It will become visible to all suppliers.');">
                                                    <input type="hidden" name="id" value="${tender.tenderId}">
                                                    <input type="hidden" name="action" value="publish">
                                                    <button type="submit" class="btn-icon btn-publish" title="Publish Tender">Publish</button>
                                                </form>
                                            </c:if>
                                            
                                            <c:if test="${tender.status == 'CLOSED' && tender.bidCount > 0}">
                                                <form action="${pageContext.request.contextPath}/officer/tender-detail" method="POST" 
                                                      style="display: inline;" onsubmit="return confirm('Start evaluation for this tender?');">
                                                    <input type="hidden" name="id" value="${tender.tenderId}">
                                                    <input type="hidden" name="action" value="startEvaluation">
                                                    <button type="submit" class="btn-icon btn-evaluate" title="Start Evaluation">Evaluate</button>
                                                </form>
                                            </c:if>
                                            
                                            <c:if test="${tender.status == 'EVALUATED'}">
                                                <a href="${pageContext.request.contextPath}/officer/award-tender?id=${tender.tenderId}" 
                                                   class="btn-icon btn-award" title="Award Contract">Award</a>
                                            </c:if>
                                            
                                            <c:if test="${tender.status == 'AWARDED'}">
                                                <a href="${pageContext.request.contextPath}/officer/award-notice?id=${tender.tenderId}" 
                                                   class="btn-icon" title="View Award Notice">Notice</a>
                                            </c:if>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </c:when>
                    <c:otherwise>
                        <div class="empty-state">
                            <div class="empty-icon">!</div>
                            <h3>No Tenders Found</h3>
                            <p>
                                <c:choose>
                                    <c:when test="${not empty param.status or not empty param.category or not empty param.keyword}">
                                        No tenders match your filter criteria. Try adjusting your filters.
                                    </c:when>
                                    <c:otherwise>
                                        You haven't created any tenders yet.
                                    </c:otherwise>
                                </c:choose>
                            </p>
                            <a href="${pageContext.request.contextPath}/officer/create-tender" class="btn btn-primary">
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
                           class="page-link">Previous</a>
                    </c:if>
                    
                    <c:forEach begin="1" end="${totalPages}" var="page">
                        <c:choose>
                            <c:when test="${page == currentPage}">
                                <span class="page-link active">${page}</span>
                            </c:when>
                            <c:otherwise>
                                <a href="${pageContext.request.contextPath}/officer/tenders?page=${page}&status=${param.status}&category=${param.category}&keyword=${param.keyword}" 
                                   class="page-link">${page}</a>
                            </c:otherwise>
                        </c:choose>
                    </c:forEach>
                    
                    <c:if test="${currentPage < totalPages}">
                        <a href="${pageContext.request.contextPath}/officer/tenders?page=${currentPage + 1}&status=${param.status}&category=${param.category}&keyword=${param.keyword}" 
                           class="page-link">Next</a>
                    </c:if>
                </div>
            </c:if>

            <!-- Summary Stats -->
            <c:if test="${not empty tenderList}">
                <div class="summary-stats">
                    <p>
                        Showing ${tenderList.size()} of ${totalTenders} tender${totalTenders != 1 ? 's' : ''}
                        <c:if test="${not empty param.status}">
                            with status "${param.status}"
                        </c:if>
                        <c:if test="${not empty param.category}">
                            in category "${param.category}"
                        </c:if>
                    </p>
                </div>
            </c:if>

        </main>
    </div>

    <jsp:include page="/shared/footer.jsp" />

    <script>
        document.getElementById('statusFilter').addEventListener('change', function() {
            this.form.submit();
        });
        
        document.getElementById('categoryFilter').addEventListener('change', function() {
            this.form.submit();
        });
        
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