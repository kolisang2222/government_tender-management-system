<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<h2 class="section-title">Supplier Report</h2>

<c:choose>
    <c:when test="${not empty supplierReportList}">
        <table>
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
                        <td><fmt:formatDate value="${supplier.createdAt}" pattern="dd MMM yyyy"/></td>
                        <td>${supplier.bidsSubmitted}</td>
                        <td>${supplier.awardsWon}</td>
                        <td>${supplier.formattedTotalAwardValue}</td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </c:when>
    <c:otherwise>
        <p class="no-data">No suppliers found</p>
    </c:otherwise>
</c:choose>