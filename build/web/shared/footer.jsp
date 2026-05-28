<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/footer.css">

<footer>
    <div class="footer-container">
        <div class="footer-columns">
            <div class="footer-column">
                <h4>ProcureGov</h4>
                <p>Government e-Tender Management System</p>
                <p>Ministry of Public Works, Kingdom of Lesotho</p>
            </div>

            <div class="footer-column">
                <h4>Quick Links</h4>
                <ul>
                    <li><a href="${pageContext.request.contextPath}/">Home</a></li>
                    <li><a href="${pageContext.request.contextPath}/about.jsp">About ProcureGov</a></li>
                    <li><a href="${pageContext.request.contextPath}/supplier/tenders">Tender Notices</a></li>
                    <li><a href="${pageContext.request.contextPath}/contact.jsp">Contact ICT Directorate</a></li>
                </ul>
            </div>

            <div class="footer-column">
                <h4>Legal</h4>
                <ul>
                    <li><a href="${pageContext.request.contextPath}/terms.jsp">Terms of Use</a></li>
                    <li><a href="${pageContext.request.contextPath}/privacy.jsp">Privacy Policy</a></li>
                    <li><a href="${pageContext.request.contextPath}/accessibility.jsp">Accessibility</a></li>
                    <li><a href="${pageContext.request.contextPath}/regulations.jsp">Procurement Regulations</a></li>
                </ul>
            </div>

            <div class="footer-column">
                <h4>Contact</h4>
                <p><span class="footer-icon">📞</span> +266 5740 8184</p>
                <p><span class="footer-icon">📧</span> kolisang.phatela@bothouniversity.com</p>
                <p><span class="footer-icon">📍</span> Ministry of Public Works, Maseru, Lesotho</p>
                <p><span class="footer-icon">🕐</span> Mon-Fri, 08:00 - 16:30</p>
            </div>
        </div>

        <div class="footer-bottom">
            <p>&copy; <fmt:formatDate value="<%= new java.util.Date()%>" pattern="yyyy"/> 
                Ministry of Public Works, Kingdom of Lesotho. All rights reserved.</p>
            <p class="footer-warning">
                <small>
                    This system is for authorized use only. All activities are logged and monitored.
                    Unauthorized access is prohibited and may result in legal action.
                </small>
            </p>
        </div>
    </div>
</footer>