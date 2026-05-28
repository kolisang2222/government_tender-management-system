<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<link rel="stylesheet" href="procgov-dashboard.css">

<c:if test="${not empty successMessage}">
    <div class="alert alert-success" role="alert">
        <span>✅</span>
        <span>${successMessage}</span>
        <button type="button" onclick="this.parentElement.remove()">×</button>
    </div>
</c:if>

<c:if test="${not empty errorMessage}">
    <div class="alert alert-error" role="alert">
        <span>⚠️</span>
        <span>${errorMessage}</span>
        <button type="button" onclick="this.parentElement.remove()">×</button>
    </div>
</c:if>

<c:if test="${not empty warningMessage}">
    <div class="alert alert-warning" role="alert">
        <span>⚠️</span>
        <span>${warningMessage}</span>
        <button type="button" onclick="this.parentElement.remove()">×</button>
    </div>
</c:if>

<c:if test="${not empty infoMessage}">
    <div class="alert alert-info" role="alert">
        <span>ℹ️</span>
        <span>${infoMessage}</span>
        <button type="button" onclick="this.parentElement.remove()">×</button>
    </div>
</c:if>

<c:remove var="successMessage" scope="session" />
<c:remove var="errorMessage" scope="session" />
<c:remove var="warningMessage" scope="session" />
<c:remove var="infoMessage" scope="session" />