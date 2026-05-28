<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<style>
    /* --- THE HEADER COMMAND CENTER --- */
header {
    background-color: #001489; /* Official Navy Blue */
    color: white;
    padding: 15px 0;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
    font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

/* Main container to keep content centered */
header > div {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 20px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    flex-wrap: wrap; /* Good for mobile responsiveness */
}

/* --- LEFT SIDE: THE BRANDING --- */
header > div > div:first-child {
    display: flex;
    align-items: center;
    gap: 20px;
}

/* Placeholder for a logo (the empty div in your HTML) */
header > div > div:first-child > div:first-child {
    width: 50px;
    height: 50px;
    background-color: white;
    border-radius: 50%;
    /* Add a background-image here later if you get a logo file */
    display: flex;
    align-items: center;
    justify-content: center;
    box-shadow: 0 2px 4px rgba(0,0,0,0.2);
}

header h1 {
    font-size: 20px;
    margin: 0;
    letter-spacing: 1px;
    font-weight: 700;
}

header h2 {
    font-size: 15px;
    margin: 2px 0;
    color: #cbd5e0;
    font-weight: 400;
}

header p {
    margin: 0;
    font-size: 13px;
    color: #68d391; /* Soft Green accent */
    font-weight: 500;
}

/* --- RIGHT SIDE: USER PROFILE OR CONTACT --- */
header > div > div:last-child {
    display: flex;
    align-items: center;
}

/* Guest State: ICT Helpdesk Info */
header > div > div:last-child > div {
    text-align: right;
}

header > div > div:last-child p {
    color: #cbd5e0;
    font-size: 13px;
    margin: 2px 0;
}

/* Logged-in State: User Profile Badge */
header div:has(> span:contains('👤')) {
    background-color: rgba(255, 255, 255, 0.1);
    padding: 8px 15px;
    border-radius: 50px; /* Pill shape */
    display: flex;
    align-items: center;
    gap: 12px;
    border: 1px solid rgba(255, 255, 255, 0.2);
    transition: background 0.3s ease;
}

header div:has(> span:contains('👤')):hover {
    background-color: rgba(255, 255, 255, 0.2);
}

/* User Icon */
header span:contains('👤') {
    font-size: 20px;
    background: white;
    width: 35px;
    height: 35px;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 50%;
    box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

/* User Name and Role Text */
header div div span:first-child {
    display: block;
    font-weight: 600;
    font-size: 14px;
    color: #ffffff;
}

header div div span:last-child {
    display: block;
    font-size: 11px;
    text-transform: uppercase;
    letter-spacing: 0.5px;
    color: #68d391; /* Role color matches sub-header green */
}

/* --- MOBILE OPTIMIZATION --- */
@media (max-width: 768px) {
    header > div {
        flex-direction: column;
        text-align: center;
        gap: 20px;
    }
    
    header > div > div:first-child {
        flex-direction: column;
        gap: 10px;
    }

    header > div > div:last-child > div {
        text-align: center;
    }
}
</style>
<link rel="stylesheet" href="procgov-dashboard.css">
<header>
    <div>
        <div>
            <div></div>
            <div>
                <h1>MINISTRY OF PUBLIC WORKS</h1>
                <h2>KINGDOM OF LESOTHO</h2>
                <p>ProcureGov — Government e-Tender Management System</p>
            </div>
        </div>
        
        <div>
            <c:if test="${not empty sessionScope.user}">
                <div>
                    <span>👤</span>
                    <div>
                        <span>${sessionScope.userName}</span>
                        <span>${sessionScope.userRole}</span>
                    </div>
                </div>
            </c:if>
            
            <c:if test="${empty sessionScope.user}">
                <div>
                    <p>ICT Helpdesk: +266 5740 8184</p>
                    <p>kolisang.phatela@bothouniversity.com</p>
                </div>
            </c:if>
        </div>
    </div>
</header>