<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<h2 class="section-title">Awards Summary</h2>

<div class="summary-cards">
    <div class="summary-card">
        <span class="card-value">${awardStats.totalAwards}</span>
        <span class="card-label">Total Awards</span>
    </div>
    <div class="summary-card">
        <span class="card-value">${awardStats.totalValue}</span>
        <span class="card-label">Total Award Value</span>
    </div>
    <div class="summary-card">
        <span class="card-value">${awardStats.avgAwardValue}</span>
        <span class="card-label">Average Award Value</span>
    </div>
</div>

<h2 class="section-title">Award Details</h2>

<c:choose>
    <c:when test="${not empty awardReportList}">
        <table>
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
                        <td><fmt:formatDate value="${award.awardDate}" pattern="dd MMM yyyy"/></td>
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
        <p class="no-data">No awards match the selected filters</p>
    </c:otherwise>
</c:choose>