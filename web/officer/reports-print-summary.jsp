<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<h2 class="section-title">Executive Summary</h2>

<div class="summary-cards">
    <div class="summary-card">
        <span class="card-value">${summaryStats.totalTenders}</span>
        <span class="card-label">Total Tenders</span>
    </div>
    <div class="summary-card">
        <span class="card-value">${summaryStats.openTenders}</span>
        <span class="card-label">Open Tenders</span>
    </div>
    <div class="summary-card">
        <span class="card-value">${summaryStats.awardedTenders}</span>
        <span class="card-label">Awarded Tenders</span>
    </div>
    <div class="summary-card">
        <span class="card-value">${summaryStats.totalAwardValue}</span>
        <span class="card-label">Total Award Value</span>
    </div>
</div>

<div class="summary-cards">
    <div class="summary-card">
        <span class="card-value">${summaryStats.totalBids}</span>
        <span class="card-label">Total Bids Received</span>
    </div>
    <div class="summary-card">
        <span class="card-value">${summaryStats.registeredSuppliers}</span>
        <span class="card-label">Registered Suppliers</span>
    </div>
    <div class="summary-card">
        <span class="card-value">${summaryStats.pendingEvaluations}</span>
        <span class="card-label">Pending Evaluations</span>
    </div>
    <div class="summary-card">
        <span class="card-value">${summaryStats.avgBidsPerTender}</span>
        <span class="card-label">Avg Bids per Tender</span>
    </div>
</div>

<h2 class="section-title">Tenders by Category</h2>
<c:choose>
    <c:when test="${not empty categoryStats}">
        <table>
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

<h2 class="section-title">Monthly Tender Activity</h2>
<c:choose>
    <c:when test="${not empty monthlyStats}">
        <table>
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