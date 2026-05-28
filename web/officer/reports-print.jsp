<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>ProcureGov Report - ${currentTab}</title>
    <style>
        /* Print-friendly styles */
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        
        body {
            font-family: 'Segoe UI', Arial, sans-serif;
            margin: 0;
            padding: 20px;
            color: #000;
            background: white;
        }
        
        .report-container {
            max-width: 1200px;
            margin: 0 auto;
            background: white;
        }
        
        .report-header {
            text-align: center;
            margin-bottom: 30px;
            border-bottom: 3px solid #00529B;
            padding-bottom: 20px;
        }
        
        .report-header h1 {
            color: #00529B;
            margin: 0 0 5px 0;
            font-size: 28px;
        }
        
        .report-header h2 {
            margin: 5px 0;
            font-weight: normal;
            font-size: 18px;
            color: #333;
        }
        
        .report-header h3 {
            margin: 5px 0;
            font-weight: normal;
            font-size: 16px;
            color: #666;
        }
        
        .report-meta {
            margin-bottom: 25px;
            padding: 10px;
            background: #f9f9f9;
            border: 1px solid #ddd;
            font-size: 13px;
        }
        
        .report-meta p {
            margin: 5px 0;
        }
        
        .section-title {
            font-size: 20px;
            color: #00529B;
            margin: 25px 0 15px 0;
            padding-bottom: 8px;
            border-bottom: 2px solid #00529B;
        }
        
        table {
            width: 100%;
            border-collapse: collapse;
            margin-bottom: 20px;
            font-size: 12px;
        }
        
        th {
            background: #00529B;
            color: white;
            padding: 10px;
            text-align: left;
            font-weight: bold;
        }
        
        td {
            padding: 8px 10px;
            border: 1px solid #ddd;
        }
        
        tr:nth-child(even) {
            background: #f9f9f9;
        }
        
        .summary-cards {
            display: flex;
            flex-wrap: wrap;
            gap: 15px;
            margin-bottom: 25px;
        }
        
        .summary-card {
            flex: 1;
            min-width: 150px;
            background: #f5f5f5;
            border: 1px solid #ddd;
            padding: 15px;
            text-align: center;
            border-radius: 5px;
        }
        
        .card-value {
            display: block;
            font-size: 24px;
            font-weight: bold;
            color: #00529B;
            margin-bottom: 5px;
        }
        
        .card-label {
            display: block;
            font-size: 12px;
            color: #666;
        }
        
        .report-footer {
            margin-top: 40px;
            text-align: center;
            font-size: 11px;
            color: #666;
            border-top: 1px solid #ddd;
            padding-top: 15px;
        }
        
        .status-badge {
            display: inline-block;
            padding: 3px 8px;
            border-radius: 3px;
            font-size: 11px;
            font-weight: bold;
        }
        
        .status-open { background: #d4edda; color: #155724; }
        .status-closed { background: #fff3cd; color: #856404; }
        .status-awarded { background: #d1ecf1; color: #0c5460; }
        
        .positive { color: #28a745; font-weight: bold; }
        .negative { color: #dc3545; font-weight: bold; }
        
        .no-data {
            text-align: center;
            padding: 40px;
            color: #999;
            font-style: italic;
        }
        
        @media print {
            body {
                margin: 0;
                padding: 0;
            }
            
            .report-container {
                margin: 0;
                padding: 0;
            }
            
            .no-print {
                display: none;
            }
            
            th {
                background: #00529B !important;
                color: white !important;
                -webkit-print-color-adjust: exact;
                print-color-adjust: exact;
            }
            
            .summary-card {
                background: #f5f5f5 !important;
                -webkit-print-color-adjust: exact;
                print-color-adjust: exact;
            }
        }
        
        .no-print {
            text-align: center;
            margin-top: 30px;
            padding: 20px;
            border-top: 1px solid #ddd;
        }
        
        .btn-print, .btn-close {
            padding: 10px 20px;
            margin: 0 10px;
            font-size: 14px;
            cursor: pointer;
            border: none;
            border-radius: 5px;
        }
        
        .btn-print {
            background: #00529B;
            color: white;
        }
        
        .btn-close {
            background: #6c757d;
            color: white;
        }
        
        .btn-print:hover, .btn-close:hover {
            opacity: 0.9;
        }
    </style>
</head>
<body>
    <div class="report-container">
        <div class="report-header">
            <h1>MINISTRY OF PUBLIC WORKS</h1>
            <h2>KINGDOM OF LESOTHO</h2>
            <h3>ProcureGov - Official Report</h3>
        </div>

        <div class="report-meta">
            <p><strong>Report Type:</strong> ${currentTab}</p>
            <p><strong>Generated:</strong> <fmt:formatDate value="<%= new java.util.Date() %>" pattern="dd MMM yyyy, HH:mm:ss"/></p>
            <p><strong>Generated By:</strong> ${userName}</p>
        </div>

        <!-- Content based on tab -->
        <c:choose>
            <c:when test="${currentTab == 'summary'}">
                <jsp:include page="reports-print-summary.jsp" />
            </c:when>
            <c:when test="${currentTab == 'tenders'}">
                <jsp:include page="reports-print-tenders.jsp" />
            </c:when>
            <c:when test="${currentTab == 'awards'}">
                <jsp:include page="reports-print-awards.jsp" />
            </c:when>
            <c:when test="${currentTab == 'suppliers'}">
                <jsp:include page="reports-print-suppliers.jsp" />
            </c:when>
            <c:when test="${currentTab == 'evaluation'}">
                <jsp:include page="reports-print-evaluation.jsp" />
            </c:when>
        </c:choose>

        <div class="report-footer">
            <p>This is an official report generated from the ProcureGov Tender Management System.</p>
            <p>Ministry of Public Works, P.O. Box 20, Maseru 100, Lesotho</p>
            <p>Tel: +266 22 123 456 | Email: kolisang.phatela@bothouniversity.</p>
        </div>
    </div>

    <div class="no-print">
        <button class="btn-print" onclick="window.print()">🖨️ Print / Save as PDF</button>
        <button class="btn-close" onclick="window.close()">Close</button>
    </div>

    <script>
        // Auto-trigger print dialog when page loads (optional)
        // window.onload = function() { window.print(); }
    </script>
</body>
</html>