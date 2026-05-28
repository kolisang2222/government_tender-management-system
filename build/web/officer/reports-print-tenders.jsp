<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<h2 class="section-title">Tender Report</h2>

<c:choose>
    <c:when test="${not empty tenderReportList}">
        <table>
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
                        <td><fmt:formatDate value="${tender.createdAt}" pattern="dd MMM yyyy"/></td>
                        <td><fmt:formatDate value="${tender.closingDateTime}" pattern="dd MMM yyyy"/></td>
                        <td>${tender.bidCount}</td>
                        <td>${tender.formattedEstimatedValue}</td>
                        <td>${tender.formattedAwardedValue}</td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </c:when>
    <c:otherwise>
        <p class="no-data">No tenders match the selected filters</p>
    </c:otherwise>
</c:choose>