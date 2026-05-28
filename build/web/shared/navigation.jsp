<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:set var="currentPage" value="${pageContext.request.servletPath}" scope="request" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/navigation.css">
<link rel="stylesheet" href="procgov-dashboard.css">

<nav>
    <div>
        <div>
            <a href="${pageContext.request.contextPath}/">
                <span>🏛️</span>
                <span>ProcureGov</span>
            </a>
        </div>
        
        <ul>
            <c:choose>
                <c:when test="${sessionScope.userRole == 'SUPPLIER'}">
                    <li>
                        <a href="${pageContext.request.contextPath}/supplier/dashboard" 
                           class="${currentPage.contains('/supplier/dashboard') ? 'active' : ''}">
                            <span>📊</span>
                            <span>Dashboard</span>
                        </a>
                    </li>
                    <li>
                        <a href="${pageContext.request.contextPath}/supplier/tenders" 
                           class="${currentPage.contains('/supplier/tenders') ? 'active' : ''}">
                            <span>📋</span>
                            <span>Browse Tenders</span>
                        </a>
                    </li>
                    <li>
                        <a href="${pageContext.request.contextPath}/supplier/bids" 
                           class="${currentPage.contains('/supplier/bids') ? 'active' : ''}">
                            <span>📤</span>
                            <span>My Bids</span>
                        </a>
                    </li>
                    <li>
                        <a href="${pageContext.request.contextPath}/supplier/notifications" 
                           class="${currentPage.contains('/supplier/notifications') ? 'active' : ''}">
                            <span>🔔</span>
                            <span>Notifications</span>
                        </a>
                    </li>
                    <li>
                        <a href="${pageContext.request.contextPath}/supplier/profile" 
                           class="${currentPage.contains('/supplier/profile') ? 'active' : ''}">
                            <span>👤</span>
                            <span>Profile</span>
                        </a>
                    </li>
                </c:when>
                
                <c:when test="${sessionScope.userRole == 'PROCUREMENT_OFFICER'}">
                    <li>
                        <a href="${pageContext.request.contextPath}/officer/dashboard" 
                           class="${currentPage.contains('/officer/dashboard') ? 'active' : ''}">
                            <span>📊</span>
                            <span>Dashboard</span>
                        </a>
                    </li>
                    <li>
                        <a href="${pageContext.request.contextPath}/officer/tenders" 
                           class="${currentPage.contains('/officer/tenders') ? 'active' : ''}">
                            <span>📋</span>
                            <span>Manage Tenders</span>
                        </a>
                    </li>
                    <li>
                        <a href="${pageContext.request.contextPath}/officer/create-tender" 
                           class="${currentPage.contains('/officer/create-tender') ? 'active' : ''}">
                            <span>➕</span>
                            <span>Create Tender</span>
                        </a>
                    </li>
                    <li>
                        <a href="${pageContext.request.contextPath}/officer/evaluations" 
                           class="${currentPage.contains('/officer/evaluations') ? 'active' : ''}">
                            <span>⭐</span>
                            <span>Evaluations</span>
                        </a>
                    </li>
                    <li>
                        <a href="${pageContext.request.contextPath}/officer/reports" 
                           class="${currentPage.contains('/officer/reports') ? 'active' : ''}">
                            <span>📈</span>
                            <span>Reports</span>
                        </a>
                    </li>
                </c:when>
                
                <c:when test="${sessionScope.userRole == 'EVALUATION_COMMITTEE'}">
                    <li>
                        <a href="${pageContext.request.contextPath}/evaluator/dashboard" 
                           class="${currentPage.contains('/evaluator/dashboard') ? 'active' : ''}">
                            <span>📊</span>
                            <span>Dashboard</span>
                        </a>
                    </li>
                    <li>
                        <a href="${pageContext.request.contextPath}/evaluator/tenders" 
                           class="${currentPage.contains('/evaluator/tenders') ? 'active' : ''}">
                            <span>📋</span>
                            <span>Assigned Tenders</span>
                        </a>
                    </li>
                    <li>
                        <a href="${pageContext.request.contextPath}/evaluator/evaluations" 
                           class="${currentPage.contains('/evaluator/evaluations') ? 'active' : ''}">
                            <span>⭐</span>
                            <span>My Evaluations</span>
                        </a>
                    </li>
                    <li>
                        <a href="${pageContext.request.contextPath}/evaluator/results" 
                           class="${currentPage.contains('/evaluator/results') ? 'active' : ''}">
                            <span>🏆</span>
                            <span>Results</span>
                        </a>
                    </li>
                </c:when>
            </c:choose>
        </ul>
        
        <div>
            <c:if test="${not empty sessionScope.user}">
                <div>
                    <span>👤</span>
                    <span>${sessionScope.userName}</span>
                </div>
                <a href="${pageContext.request.contextPath}/logout">
                    <span>🚪</span>
                    <span>Logout</span>
                </a>
            </c:if>
        </div>
    </div>
</nav>