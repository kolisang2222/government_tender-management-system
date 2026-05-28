<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<h2 class="section-title">Evaluator Performance Summary</h2>

<c:choose>
    <c:when test="${not empty evaluatorStats}">
        <table>
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
                        <td><fmt:formatDate value="${evaluator.lastEvaluationDate}" pattern="dd MMM yyyy"/></td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </c:when>
    <c:otherwise>
        <p class="no-data">No evaluation data available</p>
    </c:otherwise>
</c:choose>

<h2 class="section-title">Completed Evaluations</h2>

<c:choose>
    <c:when test="${not empty evaluationReportList}">
        <table>
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
                        <td><fmt:formatDate value="${eval.evaluationDate}" pattern="dd MMM yyyy"/></td>
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