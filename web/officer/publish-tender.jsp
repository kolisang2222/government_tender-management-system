<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Publish Tender - ProcureGov</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/ministry-style.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/dashboard.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/publish-tender.css">
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
                        <a href="${pageContext.request.contextPath}/officer/tender-detail?id=${tender.tenderId}" class="back-link">
                            ← Back to Tender Details
                        </a>
                        <h1>Publish Tender</h1>
                        <p>Review and confirm publication</p>
                    </div>
                </div>

                <c:if test="${not empty tender}">

                    <!-- Warning Box -->
                    <div class="warning-card">
                        <div class="warning-icon">!</div>
                        <div class="warning-content">
                            <h3>Important Information</h3>
                            <ul>
                                <li>Once published, the tender will be visible to all registered suppliers.</li>
                                <li>The tender details cannot be edited after publication.</li>
                                <li>Suppliers can submit bids until the closing date.</li>
                            </ul>
                        </div>
                    </div>

                    <!-- Tender Summary Card -->
                    <div class="summary-card">
                        <div class="summary-header">
                            <h2>Tender Summary</h2>
                            <span class="status-badge status-draft">Draft</span>
                        </div>

                        <div class="summary-details">
                            <div class="detail-row">
                                <span class="detail-label">Reference Number</span>
                                <span class="detail-value highlight">${tender.referenceNumber}</span>
                            </div>
                            <div class="detail-row">
                                <span class="detail-label">Title</span>
                                <span class="detail-value">${tender.title}</span>
                            </div>
                            <div class="detail-row">
                                <span class="detail-label">Category</span>
                                <span class="detail-value">${tender.category.displayName}</span>
                            </div>
                            <div class="detail-row">
                                <span class="detail-label">Estimated Value</span>
                                <span class="detail-value amount">${tender.formattedEstimatedValue}</span>
                            </div>
                            <div class="detail-row">
                                <span class="detail-label">Closing Date</span>
                                <span class="detail-value">${tender.formattedClosingDateTime}</span>
                            </div>
                        </div>
                    </div>

                    <!-- Publication Form -->
                    <div class="form-card">
                        <div class="form-card-header">
                            <h3>Publication Options</h3>
                        </div>

                        <form action="${pageContext.request.contextPath}/officer/publish-tender" method="POST">
                            <input type="hidden" name="tenderId" value="${tender.tenderId}">
                            <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">

                            <div class="form-body">
                                <!-- Notify Suppliers Option -->
                                <div class="option-card">
                                    <label class="checkbox-label">
                                        <input type="checkbox" name="notifySuppliers" value="true" checked>
                                        <span class="checkbox-custom"></span>
                                        <span class="checkbox-text">
                                            <strong>Notify all registered suppliers via email</strong>
                                            <small>Suppliers will receive an email with tender details and a link to view the full notice.</small>
                                        </span>
                                    </label>
                                </div>

                                <!-- Supplier Count Info -->
                                <div class="info-note">
                                    <span class="info-icon">i</span>
                                    <span>This tender will be visible to all ${supplierCount} registered suppliers.</span>
                                </div>
                            </div>

                            <div class="form-actions">
                                <button type="submit" class="btn btn-primary btn-lg">
                                    Confirm & Publish Tender
                                </button>
                                <a href="${pageContext.request.contextPath}/officer/tender-detail?id=${tender.tenderId}" 
                                   class="btn btn-secondary btn-lg">
                                    Cancel
                                </a>
                            </div>
                        </form>
                    </div>

                </c:if>

                <c:if test="${empty tender}">
                    <div class="error-card">
                        <div class="error-icon">!</div>
                        <h3>Tender Not Found</h3>
                        <p>The tender you are trying to publish could not be found.</p>
                        <a href="${pageContext.request.contextPath}/officer/tenders" class="btn btn-primary">
                            Back to Tenders
                        </a>
                    </div>
                </c:if>

            </main>
        </div>

        <jsp:include page="/shared/footer.jsp" />

    </body>
</html>