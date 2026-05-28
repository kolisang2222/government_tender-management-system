<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Reports - ProcureGov</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/ministry-style.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/officerdashboard.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/reports.css">
    </head>
    <body>

        <jsp:include page="/shared/navigation.jsp" />

        <div class="dashboard-layout">

            <jsp:include page="/shared/sidebar.jsp" />

            <main class="dashboard-main">

                <!-- Page Header -->
                <div class="page-header">
                    <div class="header-left">
                        <h1>📊 Reports & Analytics</h1>
                        <p>Generate and export procurement reports</p>
                    </div>
                    <div class="header-right">
                        <button class="btn btn-outline" onclick="printView()">
                            🖨️ Print View
                        </button>
                        <button class="btn btn-primary" onclick="exportPDF()">
                            📥 Export PDF
                        </button>
                        <button class="btn btn-secondary" onclick="exportCSV()">
                            📊 Export CSV
                        </button>
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

                <!-- Report Type Tabs -->
                <div class="report-tabs">
                    <a href="javascript:void(0)" class="tab-link active" data-tab="summary">
                        <span>📈</span> Summary Report
                    </a>
                    <a href="javascript:void(0)" class="tab-link" data-tab="tenders">
                        <span>📋</span> Tender Report
                    </a>
                    <a href="javascript:void(0)" class="tab-link" data-tab="awards">
                        <span>🏆</span> Awards Report
                    </a>
                    <a href="javascript:void(0)" class="tab-link" data-tab="suppliers">
                        <span>🏢</span> Supplier Report
                    </a>
                    <a href="javascript:void(0)" class="tab-link" data-tab="evaluation">
                        <span>⭐</span> Evaluation Report
                    </a>
                </div>

                <!-- ======================================================== -->
                <!-- SUMMARY REPORT TAB                                        -->
                <!-- ======================================================== -->
                <div id="summary" class="tab-content active">

                    <!-- Date Range Filter -->
                    <div class="filter-section">
                        <form action="${pageContext.request.contextPath}/officer/reports" method="GET" class="filter-form" id="summaryForm">
                            <input type="hidden" name="tab" value="summary">
                            <div class="filter-row">
                                <div class="filter-group">
                                    <label for="dateFrom">From Date</label>
                                    <input type="date" id="dateFrom" name="dateFrom" value="${param.dateFrom}">
                                </div>
                                <div class="filter-group">
                                    <label for="dateTo">To Date</label>
                                    <input type="date" id="dateTo" name="dateTo" value="${param.dateTo}">
                                </div>
                                <div class="filter-actions">
                                    <button type="submit" class="btn btn-primary">🔍 Generate</button>
                                    <a href="${pageContext.request.contextPath}/officer/reports?tab=summary" class="btn btn-secondary">Reset</a>
                                </div>
                            </div>
                        </form>
                    </div>

                    <!-- Summary Cards -->
                    <div class="summary-cards">
                        <div class="summary-card">
                            <div class="card-icon">📋</div>
                            <div class="card-content">
                                <span class="card-value">${summaryStats.totalTenders}</span>
                                <span class="card-label">Total Tenders</span>
                            </div>
                        </div>
                        <div class="summary-card">
                            <div class="card-icon">📢</div>
                            <div class="card-content">
                                <span class="card-value">${summaryStats.openTenders}</span>
                                <span class="card-label">Open Tenders</span>
                            </div>
                        </div>
                        <div class="summary-card">
                            <div class="card-icon">✅</div>
                            <div class="card-content">
                                <span class="card-value">${summaryStats.awardedTenders}</span>
                                <span class="card-label">Awarded Tenders</span>
                            </div>
                        </div>
                        <div class="summary-card">
                            <div class="card-icon">💰</div>
                            <div class="card-content">
                                <span class="card-value">${summaryStats.totalAwardValue}</span>
                                <span class="card-label">Total Award Value</span>
                            </div>
                        </div>
                    </div>

                    <!-- Second Row Stats -->
                    <div class="summary-cards">
                        <div class="summary-card">
                            <div class="card-icon">📦</div>
                            <div class="card-content">
                                <span class="card-value">${summaryStats.totalBids}</span>
                                <span class="card-label">Total Bids Received</span>
                            </div>
                        </div>
                        <div class="summary-card">
                            <div class="card-icon">🏢</div>
                            <div class="card-content">
                                <span class="card-value">${summaryStats.registeredSuppliers}</span>
                                <span class="card-label">Registered Suppliers</span>
                            </div>
                        </div>
                        <div class="summary-card">
                            <div class="card-icon">⭐</div>
                            <div class="card-content">
                                <span class="card-value">${summaryStats.pendingEvaluations}</span>
                                <span class="card-label">Pending Evaluations</span>
                            </div>
                        </div>
                        <div class="summary-card">
                            <div class="card-icon">📊</div>
                            <div class="card-content">
                                <span class="card-value">${summaryStats.avgBidsPerTender}</span>
                                <span class="card-label">Avg Bids per Tender</span>
                            </div>
                        </div>
                    </div>

                    <!-- Tenders by Category Chart -->
                    <div class="chart-section">
                        <h3>📊 Tenders by Category</h3>
                        <div class="chart-container">
                            <c:choose>
                                <c:when test="${not empty categoryStats}">
                                    <table class="stats-table">
                                        <thead>
                                            <tr>
                                                <th>Category</th>
                                                <th>Total Tenders</th>
                                                <th>Open</th>
                                                <th>Awarded</th>
                                                <th>Total Value</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach var="stat" items="${categoryStats}">
                                                <tr>
                                                    <td>${stat.category}</td>
                                                    <td>${stat.totalTenders}</td>
                                                    <td>${stat.openTenders}</td>
                                                    <td>${stat.awardedTenders}</td>
                                                    <td>${stat.totalValue}</td>
                                                </tr>
                                            </c:forEach>
                                        </tbody>
                                    </table>
                                </c:when>
                                <c:otherwise>
                                    <p class="no-data">No category data available</p>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>

                    <!-- Monthly Trends -->
                    <div class="chart-section">
                        <h3>📈 Monthly Tender Activity</h3>
                        <div class="chart-container">
                            <c:choose>
                                <c:when test="${not empty monthlyStats}">
                                    <table class="stats-table">
                                        <thead>
                                            <tr>
                                                <th>Month</th>
                                                <th>Tenders Published</th>
                                                <th>Bids Received</th>
                                                <th>Awards Made</th>
                                                <th>Award Value</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach var="stat" items="${monthlyStats}">
                                                <tr>
                                                    <td>${stat.month}</td>
                                                    <td>${stat.tendersPublished}</td>
                                                    <td>${stat.bidsReceived}</td>
                                                    <td>${stat.awardsMade}</td>
                                                    <td>${stat.awardValue}</td>
                                                </tr>
                                            </c:forEach>
                                        </tbody>
                                    </table>
                                </c:when>
                                <c:otherwise>
                                    <p class="no-data">No monthly data available</p>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </div>

                <!-- ======================================================== -->
                <!-- TENDER REPORT TAB                                         -->
                <!-- ======================================================== -->
                <div id="tenders" class="tab-content">

                    <div class="filter-section">
                        <form action="${pageContext.request.contextPath}/officer/reports" method="GET" class="filter-form" id="tendersForm">
                            <input type="hidden" name="tab" value="tenders">
                            <div class="filter-row">
                                <div class="filter-group">
                                    <label for="tenderStatus">Status</label>
                                    <select id="tenderStatus" name="status">
                                        <option value="">All Statuses</option>
                                        <option value="DRAFT" ${param.status == 'DRAFT' ? 'selected' : ''}>Draft</option>
                                        <option value="OPEN" ${param.status == 'OPEN' ? 'selected' : ''}>Open</option>
                                        <option value="CLOSED" ${param.status == 'CLOSED' ? 'selected' : ''}>Closed</option>
                                        <option value="AWARDED" ${param.status == 'AWARDED' ? 'selected' : ''}>Awarded</option>
                                        <option value="CANCELLED" ${param.status == 'CANCELLED' ? 'selected' : ''}>Cancelled</option>
                                    </select>
                                </div>
                                <div class="filter-group">
                                    <label for="tenderCategory">Category</label>
                                    <select id="tenderCategory" name="category">
                                        <option value="">All Categories</option>
                                        <option value="CONSTRUCTION" ${param.category == 'CONSTRUCTION' ? 'selected' : ''}>Construction</option>
                                        <option value="ROADS" ${param.category == 'ROADS' ? 'selected' : ''}>Roads</option>
                                        <option value="ELECTRICAL" ${param.category == 'ELECTRICAL' ? 'selected' : ''}>Electrical</option>
                                        <option value="PLUMBING" ${param.category == 'PLUMBING' ? 'selected' : ''}>Plumbing</option>
                                        <option value="GENERAL_SERVICES" ${param.category == 'GENERAL_SERVICES' ? 'selected' : ''}>General Services</option>
                                    </select>
                                </div>
                                <div class="filter-group">
                                    <label for="dateFromTender">From Date</label>
                                    <input type="date" id="dateFromTender" name="dateFrom" value="${param.dateFrom}">
                                </div>
                                <div class="filter-group">
                                    <label for="dateToTender">To Date</label>
                                    <input type="date" id="dateToTender" name="dateTo" value="${param.dateTo}">
                                </div>
                                <div class="filter-actions">
                                    <button type="submit" class="btn btn-primary">🔍 Filter</button>
                                </div>
                            </div>
                        </form>
                    </div>

                    <div class="table-container">
                        <c:choose>
                            <c:when test="${not empty tenderReportList}">
                                <table class="report-table">
                                    <thead>
                                        <tr>
                                            <th>Reference</th>
                                            <th>Title</th>
                                            <th>Category</th>
                                            <th>Status</th>
                                            <th>Published Date</th>
                                            <th>Closing Date</th>
                                            <th>Bids</th>
                                            <th>Estimated Value</th>
                                            <th>Awarded Value</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach var="tender" items="${tenderReportList}">
                                            <tr>
                                                <td>${tender.referenceNumber}</td>
                                                <td>${tender.title}</td>
                                                <td>${tender.category.displayName}</td>
                                                <td>
                                                    <span class="status-badge status-${tender.status.name().toLowerCase()}">
                                                        ${tender.status.displayName}
                                                    </span>
                                                </td>
                                                <%-- FIXED: use formatted string getters instead of fmt:formatDate (LocalDateTime is not java.util.Date) --%>
                                                <td>${tender.formattedCreatedAt}</td>
                                                <td>${tender.formattedClosingDateTime}</td>
                                                <td>${tender.bidCount}</td>
                                                <td>${tender.formattedEstimatedValue}</td>
                                                <td>${tender.formattedAwardedValue}</td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </c:when>
                            <c:otherwise>
                                <div class="empty-state">
                                    <p>No tenders match the selected filters</p>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>

                <!-- ======================================================== -->
                <!-- AWARDS REPORT TAB                                         -->
                <!-- ======================================================== -->
                <div id="awards" class="tab-content">

                    <div class="filter-section">
                        <form action="${pageContext.request.contextPath}/officer/reports" method="GET" class="filter-form" id="awardsForm">
                            <input type="hidden" name="tab" value="awards">
                            <div class="filter-row">
                                <div class="filter-group">
                                    <label for="awardDateFrom">From Date</label>
                                    <input type="date" id="awardDateFrom" name="dateFrom" value="${param.dateFrom}">
                                </div>
                                <div class="filter-group">
                                    <label for="awardDateTo">To Date</label>
                                    <input type="date" id="awardDateTo" name="dateTo" value="${param.dateTo}">
                                </div>
                                <div class="filter-group">
                                    <label for="awardCategory">Category</label>
                                    <select id="awardCategory" name="category">
                                        <option value="">All Categories</option>
                                        <option value="CONSTRUCTION" ${param.category == 'CONSTRUCTION' ? 'selected' : ''}>Construction</option>
                                        <option value="ROADS" ${param.category == 'ROADS' ? 'selected' : ''}>Roads</option>
                                        <option value="ELECTRICAL" ${param.category == 'ELECTRICAL' ? 'selected' : ''}>Electrical</option>
                                        <option value="PLUMBING" ${param.category == 'PLUMBING' ? 'selected' : ''}>Plumbing</option>
                                        <option value="GENERAL_SERVICES" ${param.category == 'GENERAL_SERVICES' ? 'selected' : ''}>General Services</option>
                                    </select>
                                </div>
                                <div class="filter-actions">
                                    <button type="submit" class="btn btn-primary">🔍 Filter</button>
                                </div>
                            </div>
                        </form>
                    </div>

                    <!-- Awards Summary -->
                    <div class="summary-cards small">
                        <div class="summary-card">
                            <div class="card-content">
                                <span class="card-value">${awardStats.totalAwards}</span>
                                <span class="card-label">Total Awards</span>
                            </div>
                        </div>
                        <div class="summary-card">
                            <div class="card-content">
                                <span class="card-value">${awardStats.totalValue}</span>
                                <span class="card-label">Total Award Value</span>
                            </div>
                        </div>
                        <div class="summary-card">
                            <div class="card-content">
                                <span class="card-value">${awardStats.avgAwardValue}</span>
                                <span class="card-label">Average Award Value</span>
                            </div>
                        </div>
                    </div>

                    <div class="table-container">
                        <c:choose>
                            <c:when test="${not empty awardReportList}">
                                <table class="report-table">
                                    <thead>
                                        <tr>
                                            <th>Tender Reference</th>
                                            <th>Tender Title</th>
                                            <th>Category</th>
                                            <th>Winning Supplier</th>
                                            <th>Award Date</th>
                                            <th>Estimated Value</th>
                                            <th>Awarded Value</th>
                                            <th>Variance</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach var="award" items="${awardReportList}">
                                            <tr>
                                                <td>${award.tenderReference}</td>
                                                <td>${award.tenderTitle}</td>
                                                <td>${award.category}</td>
                                                <td>${award.supplierName}</td>
                                                <%-- FIXED: use formatted string getter instead of fmt:formatDate --%>
                                                <td>${award.formattedAwardDate}</td>
                                                <td>${award.formattedEstimatedValue}</td>
                                                <td>${award.formattedAwardedValue}</td>
                                                <td class="${award.variance < 0 ? 'negative' : 'positive'}">
                                                    ${award.formattedVariance}
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </c:when>
                            <c:otherwise>
                                <div class="empty-state">
                                    <p>No awards match the selected filters</p>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>

                <!-- ======================================================== -->
                <!-- SUPPLIER REPORT TAB                                       -->
                <!-- ======================================================== -->
                <div id="suppliers" class="tab-content">

                    <div class="filter-section">
                        <form action="${pageContext.request.contextPath}/officer/reports" method="GET" class="filter-form" id="suppliersForm">
                            <input type="hidden" name="tab" value="suppliers">
                            <div class="filter-row">
                                <div class="filter-group">
                                    <label for="supplierSearch">Search Supplier</label>
                                    <input type="text" id="supplierSearch" name="keyword" placeholder="Name or email..." value="${param.keyword}">
                                </div>
                                <div class="filter-actions">
                                    <button type="submit" class="btn btn-primary">🔍 Search</button>
                                </div>
                            </div>
                        </form>
                    </div>

                    <div class="table-container">
                        <c:choose>
                            <c:when test="${not empty supplierReportList}">
                                <table class="report-table">
                                    <thead>
                                        <tr>
                                            <th>Registration #</th>
                                            <th>Company Name</th>
                                            <th>Email</th>
                                            <th>Contact</th>
                                            <th>Registered Date</th>
                                            <th>Bids Submitted</th>
                                            <th>Awards Won</th>
                                            <th>Total Award Value</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach var="supplier" items="${supplierReportList}">
                                            <tr>
                                                <td>${supplier.registrationNumber}</td>
                                                <td>${supplier.fullName}</td>
                                                <td>${supplier.email}</td>
                                                <td>${supplier.contactNumber}</td>
                                                <%-- FIXED: use formatted string getter instead of fmt:formatDate --%>
                                                <td>${supplier.formattedCreatedAt}</td>
                                                <td>${supplier.bidsSubmitted}</td>
                                                <td>${supplier.awardsWon}</td>
                                                <td>${supplier.formattedTotalAwardValue}</td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </c:when>
                            <c:otherwise>
                                <div class="empty-state">
                                    <p>No suppliers found</p>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>

                <!-- ======================================================== -->
                <!-- EVALUATION REPORT TAB                                     -->
                <!-- ======================================================== -->
                <div id="evaluation" class="tab-content">

                    <div class="filter-section">
                        <form action="${pageContext.request.contextPath}/officer/reports" method="GET" class="filter-form" id="evaluationForm">
                            <input type="hidden" name="tab" value="evaluation">
                            <div class="filter-row">
                                <div class="filter-group">
                                    <label for="evalDateFrom">From Date</label>
                                    <input type="date" id="evalDateFrom" name="dateFrom" value="${param.dateFrom}">
                                </div>
                                <div class="filter-group">
                                    <label for="evalDateTo">To Date</label>
                                    <input type="date" id="evalDateTo" name="dateTo" value="${param.dateTo}">
                                </div>
                                <div class="filter-actions">
                                    <button type="submit" class="btn btn-primary">🔍 Filter</button>
                                </div>
                            </div>
                        </form>
                    </div>

                    <!-- Evaluation Performance -->
                    <div class="chart-section">
                        <h3>📊 Evaluator Performance Summary</h3>
                        <div class="table-container">
                            <c:choose>
                                <c:when test="${not empty evaluatorStats}">
                                    <table class="report-table">
                                        <thead>
                                            <tr>
                                                <th>Evaluator Name</th>
                                                <th>Evaluations Completed</th>
                                                <th>Total Bids Scored</th>
                                                <th>Average Score Given</th>
                                                <th>Last Evaluation Date</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach var="evaluator" items="${evaluatorStats}">
                                                <tr>
                                                    <td>${evaluator.name}</td>
                                                    <td>${evaluator.evaluationsCompleted}</td>
                                                    <td>${evaluator.totalBidsScored}</td>
                                                    <td>${evaluator.avgScoreGiven}</td>
                                                    <%-- FIXED: use formatted string getter instead of fmt:formatDate --%>
                                                    <td>${evaluator.formattedLastEvaluationDate}</td>
                                                </tr>
                                            </c:forEach>
                                        </tbody>
                                    </table>
                                </c:when>
                                <c:otherwise>
                                    <p class="no-data">No evaluation data available</p>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>

                    <!-- Completed Evaluations -->
                    <div class="chart-section">
                        <h3>✅ Completed Evaluations</h3>
                        <div class="table-container">
                            <c:choose>
                                <c:when test="${not empty evaluationReportList}">
                                    <table class="report-table">
                                        <thead>
                                            <tr>
                                                <th>Tender Reference</th>
                                                <th>Tender Title</th>
                                                <th>Bids Evaluated</th>
                                                <th>Evaluators</th>
                                                <th>Evaluation Date</th>
                                                <th>Winning Score</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach var="eval" items="${evaluationReportList}">
                                                <tr>
                                                    <td>${eval.tenderReference}</td>
                                                    <td>${eval.tenderTitle}</td>
                                                    <td>${eval.bidsEvaluated}</td>
                                                    <td>${eval.evaluatorCount}</td>
                                                    <%-- FIXED: use formatted string getter instead of fmt:formatDate --%>
                                                    <td>${eval.formattedEvaluationDate}</td>
                                                    <td>${eval.winningScore}</td>
                                                </tr>
                                            </c:forEach>
                                        </tbody>
                                    </table>
                                </c:when>
                                <c:otherwise>
                                    <p class="no-data">No completed evaluations</p>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </div>

            </main>
        </div>

        <jsp:include page="/shared/footer.jsp" />

        <script>
            // Get current active tab
            function getCurrentTab() {
                const activeTab = document.querySelector('.tab-content.active');
                return activeTab ? activeTab.id : 'summary';
            }

            // Get current filter parameters
            function getCurrentParams() {
                const activeTab = getCurrentTab();
                const params = new URLSearchParams();
                params.set('tab', activeTab);

                if (activeTab === 'summary') {
                    const dateFrom = document.getElementById('dateFrom')?.value;
                    const dateTo = document.getElementById('dateTo')?.value;
                    if (dateFrom)
                        params.set('dateFrom', dateFrom);
                    if (dateTo)
                        params.set('dateTo', dateTo);
                } else if (activeTab === 'tenders') {
                    const status = document.getElementById('tenderStatus')?.value;
                    const category = document.getElementById('tenderCategory')?.value;
                    const dateFrom = document.getElementById('dateFromTender')?.value;
                    const dateTo = document.getElementById('dateToTender')?.value;
                    if (status)
                        params.set('status', status);
                    if (category)
                        params.set('category', category);
                    if (dateFrom)
                        params.set('dateFrom', dateFrom);
                    if (dateTo)
                        params.set('dateTo', dateTo);
                } else if (activeTab === 'awards') {
                    const dateFrom = document.getElementById('awardDateFrom')?.value;
                    const dateTo = document.getElementById('awardDateTo')?.value;
                    const category = document.getElementById('awardCategory')?.value;
                    if (dateFrom)
                        params.set('dateFrom', dateFrom);
                    if (dateTo)
                        params.set('dateTo', dateTo);
                    if (category)
                        params.set('category', category);
                } else if (activeTab === 'suppliers') {
                    const keyword = document.getElementById('supplierSearch')?.value;
                    if (keyword)
                        params.set('keyword', keyword);
                } else if (activeTab === 'evaluation') {
                    const dateFrom = document.getElementById('evalDateFrom')?.value;
                    const dateTo = document.getElementById('evalDateTo')?.value;
                    if (dateFrom)
                        params.set('dateFrom', dateFrom);
                    if (dateTo)
                        params.set('dateTo', dateTo);
                }

                return params;
            }

            function printView() {
                const params = getCurrentParams();
                const printWindow = window.open(
                        '${pageContext.request.contextPath}/officer/reports?' + params.toString() + '&print=true',
                        '_blank',
                        'width=1200,height=800,scrollbars=yes,resizable=yes,toolbar=yes'
                        );
                if (printWindow) {
                    printWindow.focus();
                } else {
                    alert('Please allow pop-ups to view the print version.');
                }
            }

            function exportPDF() {
                const params = getCurrentParams();
                const pdfWindow = window.open(
                        '${pageContext.request.contextPath}/officer/reports?' + params.toString() + '&print=true',
                        '_blank',
                        'width=1200,height=800,scrollbars=yes,resizable=yes'
                        );
                if (pdfWindow) {
                    pdfWindow.focus();
                    pdfWindow.onload = function () {
                        setTimeout(function () {
                            pdfWindow.print();
                        }, 1000);
                    };
                } else {
                    alert('Please allow pop-ups to export PDF.');
                }
            }

            function exportCSV() {
                const params = getCurrentParams();
                params.set('export', 'csv');
                window.location.href = '${pageContext.request.contextPath}/officer/reports?' + params.toString();
            }

            // Tab switching
            document.querySelectorAll('.tab-link').forEach(tab => {
                tab.addEventListener('click', function (e) {
                    e.preventDefault();
                    document.querySelectorAll('.tab-link').forEach(t => t.classList.remove('active'));
                    document.querySelectorAll('.tab-content').forEach(c => c.classList.remove('active'));
                    this.classList.add('active');
                    const tabId = this.getAttribute('data-tab');
                    document.getElementById(tabId).classList.add('active');
                    const url = new URL(window.location.href);
                    url.searchParams.set('tab', tabId);
                    window.history.pushState({}, '', url);
                });
            });

            // Restore tab from URL on load
            const urlParams = new URLSearchParams(window.location.search);
            const tabParam = urlParams.get('tab');
            if (tabParam) {
                const tab = document.querySelector(`.tab-link[data-tab="${tabParam}"]`);
                if (tab) {
                    document.querySelectorAll('.tab-link').forEach(t => t.classList.remove('active'));
                    document.querySelectorAll('.tab-content').forEach(c => c.classList.remove('active'));
                    tab.classList.add('active');
                    const targetTab = document.getElementById(tabParam);
                    if (targetTab)
                        targetTab.classList.add('active');
                }
            }

            // Auto-dismiss alerts
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

            // Set default date range filters
            const today = new Date();
            const firstOfYear = new Date(today.getFullYear(), 0, 1);

            function setDefaultDate(inputId, dateValue) {
                const input = document.getElementById(inputId);
                if (input && !input.value) {
                    input.value = dateValue;
                }
            }

            setDefaultDate('dateFrom', firstOfYear.toISOString().split('T')[0]);
            setDefaultDate('dateTo', today.toISOString().split('T')[0]);
            setDefaultDate('dateFromTender', firstOfYear.toISOString().split('T')[0]);
            setDefaultDate('dateToTender', today.toISOString().split('T')[0]);
            setDefaultDate('awardDateFrom', firstOfYear.toISOString().split('T')[0]);
            setDefaultDate('awardDateTo', today.toISOString().split('T')[0]);
            setDefaultDate('evalDateFrom', firstOfYear.toISOString().split('T')[0]);
            setDefaultDate('evalDateTo', today.toISOString().split('T')[0]);
        </script>

    </body>
</html>
