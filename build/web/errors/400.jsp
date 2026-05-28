<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/error.css">
    <link rel="stylesheet" href="procgov-dashboard.css">
    <title>400 Bad Request - ProcureGov | Ministry of Public Works Lesotho</title>
</head>
<body>
    <main>
        <div>
            <div>
                <span>400</span>
                <h3>Bad Request</h3>
            </div>
            
            <div>
                <p>The server could not understand your request.</p>
                <p>This may be due to:</p>
                <ul>
                    <li>Malformed request syntax</li>
                    <li>Invalid request parameters</li>
                    <li>Request too large</li>
                    <li>Corrupted request data</li>
                </ul>
            </div>
            
            <div>
                <p>Please check your input and try again.</p>
                
                <div>
                    <a href="javascript:history.back()">
                        <span>←</span>
                        <span>Go Back</span>
                    </a>
                    
                    <a href="${pageContext.request.contextPath}/">
                        <span>🏠</span>
                        <span>Return to Home</span>
                    </a>
                </div>
            </div>
            
            <div>
                <p>If the problem persists, please contact ICT Support.</p>
                <p>📞 +266 5740 8184 | 📧 kolisang.phatela@bothouniversity.com</p>
            </div>
        </div>
    </main>

</body>
</html>