package controller;

import dao.DAOException;
import dao.impl.BidDAOImpl;
import dao.impl.TenderDAOImpl;
import dao.impl.UserDAOImpl;
import dao.impl.EvaluationDAOImpl;
import dao.interfaces.BidDAO;
import dao.interfaces.TenderDAO;
import dao.interfaces.UserDAO;
import dao.interfaces.EvaluationDAO;
import model.Tender;
import model.User;
import model.enums.TenderCategory;
import model.enums.TenderStatus;
import model.enums.UserRole;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@WebServlet("/officer/reports")
public class OfficerReportsServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(OfficerReportsServlet.class.getName());
    private TenderDAO tenderDAO;
    private BidDAO bidDAO;
    private UserDAO userDAO;
    private EvaluationDAO evaluationDAO;

    @Override
    public void init() throws ServletException {
        try {
            tenderDAO = new TenderDAOImpl();
            bidDAO = new BidDAOImpl();
            userDAO = new UserDAOImpl();
            evaluationDAO = new EvaluationDAOImpl();
            logger.info("DAOs initialized successfully in OfficerReportsServlet");
        } catch (DAOException e) {
            logger.log(Level.SEVERE, "Failed to initialize DAOs", e);
            throw new ServletException("Cannot initialize DAOs", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }

        User currentUser = (User) session.getAttribute("user");
        if (currentUser.getRole() != UserRole.PROCUREMENT_OFFICER) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
            return;
        }

        try {
            String tab = req.getParameter("tab");
            if (tab == null) {
                tab = "summary";
            }

            // Get filter parameters
            String dateFromParam = req.getParameter("dateFrom");
            String dateToParam = req.getParameter("dateTo");
            String statusParam = req.getParameter("status");
            String categoryParam = req.getParameter("category");
            String keyword = req.getParameter("keyword");
            String export = req.getParameter("export");
            String print = req.getParameter("print");

            LocalDate dateFrom = null;
            LocalDate dateTo = null;

            if (dateFromParam != null && !dateFromParam.trim().isEmpty()) {
                dateFrom = LocalDate.parse(dateFromParam);
            }
            if (dateToParam != null && !dateToParam.trim().isEmpty()) {
                dateTo = LocalDate.parse(dateToParam);
            }

            // Get all tenders
            List<Tender> allTenders = tenderDAO.findAll();

            // Apply date filters
            if (dateFrom != null || dateTo != null) {
                final LocalDate from = dateFrom;
                final LocalDate to = dateTo;

                allTenders = allTenders.stream()
                        .filter(t -> t.getCreatedAt() != null)
                        .filter(t -> {
                            LocalDate created = t.getCreatedAt().toLocalDate();
                            return (from == null || !created.isBefore(from))
                                    && (to == null || !created.isAfter(to));
                        })
                        .collect(Collectors.toList());
            }

            // Prepare summary statistics
            prepareSummaryStats(req, allTenders);

            // Prepare category statistics
            prepareCategoryStats(req, allTenders);

            // Prepare monthly statistics
            prepareMonthlyStats(req, allTenders);

            // Prepare tender report list
            prepareTenderReportList(req, allTenders, statusParam, categoryParam, keyword);

            // Prepare awards report list
            prepareAwardReportList(req, allTenders, dateFrom, dateTo, categoryParam);

            // Prepare supplier report list
            prepareSupplierReportList(req, keyword);

            // Prepare evaluation report list
            prepareEvaluationReportList(req, allTenders, dateFrom, dateTo);

            req.setAttribute("currentTab", tab);
            req.setAttribute("userName", currentUser.getFullName());

            // Handle print view
            if ("true".equals(print)) {
                req.getRequestDispatcher("/officer/reports-print.jsp").forward(req, resp);
                return;
            }

            // Handle CSV export
            if ("csv".equals(export)) {
                handleCsvExport(req, resp, tab, dateFrom, dateTo, statusParam, categoryParam, keyword);
                return;
            }

            req.getRequestDispatcher("/officer/reports.jsp").forward(req, resp);

        } catch (DAOException e) {
            logger.log(Level.SEVERE, "Error loading reports", e);
            req.setAttribute("errorMessage", "Error loading reports. Please try again.");
            req.getRequestDispatcher("/officer/reports.jsp").forward(req, resp);
        }
    }

    private void prepareSummaryStats(HttpServletRequest req, List<Tender> tenders) throws DAOException {
        int totalTenders = tenders.size();
        int openTenders = (int) tenders.stream().filter(t -> t.getStatus() == TenderStatus.OPEN).count();
        int awardedTenders = (int) tenders.stream().filter(t -> t.getStatus() == TenderStatus.AWARDED).count();
        int pendingEvaluations = (int) tenders.stream().filter(t -> t.getStatus() == TenderStatus.CLOSED).count();

        double totalAwardValue = tenders.stream()
                .filter(t -> t.getStatus() == TenderStatus.AWARDED)
                .filter(t -> t.getAwardedValue() != null)
                .mapToDouble(t -> t.getAwardedValue().doubleValue())
                .sum();

        long totalBids = 0;
        for (Tender tender : tenders) {
            totalBids += bidDAO.countBidsByTenderId(tender.getTenderId());
        }

        int registeredSuppliers = userDAO.countByRole(UserRole.SUPPLIER);
        double avgBidsPerTender = totalTenders > 0 ? (double) totalBids / totalTenders : 0;

        req.setAttribute("summaryStats", Map.of(
                "totalTenders", totalTenders,
                "openTenders", openTenders,
                "awardedTenders", awardedTenders,
                "totalAwardValue", String.format("M %,.2f", totalAwardValue),
                "totalBids", totalBids,
                "registeredSuppliers", registeredSuppliers,
                "pendingEvaluations", pendingEvaluations,
                "avgBidsPerTender", String.format("%.1f", avgBidsPerTender)
        ));
    }

    private void prepareCategoryStats(HttpServletRequest req, List<Tender> tenders) {
        Map<TenderCategory, List<Tender>> byCategory = tenders.stream()
                .collect(Collectors.groupingBy(Tender::getCategory));

        List<Map<String, Object>> categoryStats = new ArrayList<>();

        for (Map.Entry<TenderCategory, List<Tender>> entry : byCategory.entrySet()) {
            TenderCategory category = entry.getKey();
            List<Tender> categoryTenders = entry.getValue();

            Map<String, Object> stats = new HashMap<>();
            stats.put("category", category.getDisplayName());
            stats.put("totalTenders", categoryTenders.size());
            stats.put("openTenders", categoryTenders.stream().filter(t -> t.getStatus() == TenderStatus.OPEN).count());
            stats.put("awardedTenders", categoryTenders.stream().filter(t -> t.getStatus() == TenderStatus.AWARDED).count());

            double totalValue = categoryTenders.stream()
                    .filter(t -> t.getStatus() == TenderStatus.AWARDED)
                    .filter(t -> t.getAwardedValue() != null)
                    .mapToDouble(t -> t.getAwardedValue().doubleValue())
                    .sum();

            stats.put("totalValue", String.format("M %,.2f", totalValue));
            categoryStats.add(stats);
        }

        req.setAttribute("categoryStats", categoryStats);
    }

    private void prepareMonthlyStats(HttpServletRequest req, List<Tender> tenders) {
        DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMM yyyy");

        Map<String, List<Tender>> byMonth = tenders.stream()
                .filter(t -> t.getCreatedAt() != null)
                .collect(Collectors.groupingBy(t -> t.getCreatedAt().format(monthFormatter)));

        List<Map<String, Object>> monthlyStats = new ArrayList<>();

        for (Map.Entry<String, List<Tender>> entry : byMonth.entrySet()) {
            String month = entry.getKey();
            List<Tender> monthTenders = entry.getValue();

            Map<String, Object> stats = new HashMap<>();
            stats.put("month", month);
            stats.put("tendersPublished", monthTenders.size());

            long bidsReceived = 0;
            for (Tender tender : monthTenders) {
                try {
                    bidsReceived += bidDAO.countBidsByTenderId(tender.getTenderId());
                } catch (DAOException e) {
                    // Ignore
                }
            }
            stats.put("bidsReceived", bidsReceived);

            int awardsMade = (int) monthTenders.stream().filter(t -> t.getStatus() == TenderStatus.AWARDED).count();
            stats.put("awardsMade", awardsMade);

            double awardValue = monthTenders.stream()
                    .filter(t -> t.getStatus() == TenderStatus.AWARDED)
                    .filter(t -> t.getAwardedValue() != null)
                    .mapToDouble(t -> t.getAwardedValue().doubleValue())
                    .sum();

            stats.put("awardValue", String.format("M %,.2f", awardValue));
            monthlyStats.add(stats);
        }

        monthlyStats.sort((a, b) -> b.get("month").toString().compareTo(a.get("month").toString()));
        req.setAttribute("monthlyStats", monthlyStats);
    }

    private void prepareTenderReportList(HttpServletRequest req, List<Tender> tenders,
            String statusParam, String categoryParam, String keyword) {
        List<Tender> filtered = new ArrayList<>(tenders);

        if (statusParam != null && !statusParam.trim().isEmpty()) {
            try {
                TenderStatus status = TenderStatus.valueOf(statusParam);
                filtered = filtered.stream().filter(t -> t.getStatus() == status).collect(Collectors.toList());
            } catch (IllegalArgumentException e) {
                // Ignore
            }
        }

        if (categoryParam != null && !categoryParam.trim().isEmpty()) {
            try {
                TenderCategory category = TenderCategory.valueOf(categoryParam);
                filtered = filtered.stream().filter(t -> t.getCategory() == category).collect(Collectors.toList());
            } catch (IllegalArgumentException e) {
                // Ignore
            }
        }

        if (keyword != null && !keyword.trim().isEmpty()) {
            String lowerKeyword = keyword.toLowerCase();
            filtered = filtered.stream()
                    .filter(t -> (t.getReferenceNumber() != null && t.getReferenceNumber().toLowerCase().contains(lowerKeyword))
                    || (t.getTitle() != null && t.getTitle().toLowerCase().contains(lowerKeyword)))
                    .collect(Collectors.toList());
        }

        for (Tender tender : filtered) {
            try {
                tender.setBidCount(bidDAO.countBidsByTenderId(tender.getTenderId()));
            } catch (DAOException e) {
                tender.setBidCount(0);
            }
        }

        req.setAttribute("tenderReportList", filtered);
    }

    private void prepareAwardReportList(HttpServletRequest req, List<Tender> tenders,
            LocalDate dateFrom, LocalDate dateTo, String categoryParam) {
        List<Tender> awarded = tenders.stream()
                .filter(t -> t.getStatus() == TenderStatus.AWARDED)
                .filter(t -> t.getAwardDate() != null)
                .collect(Collectors.toList());

        if (dateFrom != null) {
            awarded = awarded.stream()
                    .filter(t -> !t.getAwardDate().toLocalDate().isBefore(dateFrom))
                    .collect(Collectors.toList());
        }

        if (dateTo != null) {
            awarded = awarded.stream()
                    .filter(t -> !t.getAwardDate().toLocalDate().isAfter(dateTo))
                    .collect(Collectors.toList());
        }

        if (categoryParam != null && !categoryParam.trim().isEmpty()) {
            try {
                TenderCategory category = TenderCategory.valueOf(categoryParam);
                awarded = awarded.stream().filter(t -> t.getCategory() == category).collect(Collectors.toList());
            } catch (IllegalArgumentException e) {
                // Ignore
            }
        }

        // Calculate award statistics
        int totalAwards = awarded.size();
        double totalValue = awarded.stream()
                .filter(t -> t.getAwardedValue() != null)
                .mapToDouble(t -> t.getAwardedValue().doubleValue())
                .sum();
        double avgValue = totalAwards > 0 ? totalValue / totalAwards : 0;

        req.setAttribute("awardStats", Map.of(
                "totalAwards", totalAwards,
                "totalValue", String.format("M %,.2f", totalValue),
                "avgAwardValue", String.format("M %,.2f", avgValue)
        ));

        // Prepare award report list with variance
        List<Map<String, Object>> awardReportList = new ArrayList<>();
        for (Tender tender : awarded) {
            Map<String, Object> award = new HashMap<>();
            award.put("tenderReference", tender.getReferenceNumber());
            award.put("tenderTitle", tender.getTitle());
            award.put("category", tender.getCategory().getDisplayName());
            award.put("supplierName", tender.getWinningSupplierName());
            award.put("awardDate", tender.getAwardDate());
            award.put("formattedEstimatedValue", tender.getFormattedEstimatedValue());
            award.put("formattedAwardedValue", tender.getFormattedAwardedValue());

            if (tender.getEstimatedValue() != null && tender.getAwardedValue() != null) {
                BigDecimal variance = tender.getAwardedValue().subtract(tender.getEstimatedValue());
                double variancePercent = variance.doubleValue() / tender.getEstimatedValue().doubleValue() * 100;
                award.put("variance", variancePercent);
                award.put("formattedVariance", String.format("%+.1f%%", variancePercent));
            } else {
                award.put("variance", 0.0);
                award.put("formattedVariance", "N/A");
            }

            awardReportList.add(award);
        }

        req.setAttribute("awardReportList", awardReportList);
    }

    private void prepareSupplierReportList(HttpServletRequest req, String keyword) throws DAOException {
        List<User> suppliers = userDAO.findByRole(UserRole.SUPPLIER);

        if (keyword != null && !keyword.trim().isEmpty()) {
            String lowerKeyword = keyword.toLowerCase();
            suppliers = suppliers.stream()
                    .filter(s -> (s.getFullName() != null && s.getFullName().toLowerCase().contains(lowerKeyword))
                    || (s.getEmail() != null && s.getEmail().toLowerCase().contains(lowerKeyword)))
                    .collect(Collectors.toList());
        }

        List<Map<String, Object>> supplierReportList = new ArrayList<>();
        for (User supplier : suppliers) {
            Map<String, Object> report = new HashMap<>();
            report.put("registrationNumber", supplier.getRegistrationNumber());
            report.put("fullName", supplier.getFullName());
            report.put("email", supplier.getEmail());
            report.put("contactNumber", supplier.getContactNumber());
            report.put("createdAt", supplier.getCreatedAt());

            List<model.Bid> bids = bidDAO.findBySupplierId(supplier.getUserId());
            report.put("bidsSubmitted", bids.size());

            int awardsWon = (int) bids.stream()
                    .filter(b -> b.getTenderStatus() == TenderStatus.AWARDED)
                    .filter(model.Bid::isWinner)
                    .count();
            report.put("awardsWon", awardsWon);

            double totalAwardValue = bids.stream()
                    .filter(b -> b.getTenderStatus() == TenderStatus.AWARDED)
                    .filter(model.Bid::isWinner)
                    .mapToDouble(b -> b.getBidAmount().doubleValue())
                    .sum();
            report.put("formattedTotalAwardValue", String.format("M %,.2f", totalAwardValue));

            supplierReportList.add(report);
        }

        req.setAttribute("supplierReportList", supplierReportList);
    }

    private void prepareEvaluationReportList(HttpServletRequest req, List<Tender> tenders,
            LocalDate dateFrom, LocalDate dateTo) throws DAOException {
        List<User> evaluators = userDAO.findByRole(UserRole.EVALUATION_COMMITTEE);

        List<Map<String, Object>> evaluatorStats = new ArrayList<>();
        for (User evaluator : evaluators) {
            Map<String, Object> stats = new HashMap<>();
            stats.put("name", evaluator.getFullName());

            List<model.EvaluationScore> scores = evaluationDAO.findByEvaluatorId(evaluator.getUserId());
            stats.put("totalBidsScored", scores.size());

            long evaluationsCompleted = scores.stream()
                    .map(model.EvaluationScore::getBidId)
                    .distinct()
                    .count();
            stats.put("evaluationsCompleted", evaluationsCompleted);

            double avgScore = scores.stream()
                    .mapToDouble(model.EvaluationScore::getWeightedTotal)
                    .average()
                    .orElse(0.0);
            stats.put("avgScoreGiven", String.format("%.2f", avgScore));

            if (!scores.isEmpty()) {
                model.EvaluationScore lastScore = scores.stream()
                        .max((s1, s2) -> s1.getSubmittedAt().compareTo(s2.getSubmittedAt()))
                        .orElse(null);
                if (lastScore != null) {
                    stats.put("lastEvaluationDate", lastScore.getSubmittedAt());
                }
            }

            evaluatorStats.add(stats);
        }

        req.setAttribute("evaluatorStats", evaluatorStats);

        // Completed evaluations list
        List<Tender> completed = tenders.stream()
                .filter(t -> t.getStatus() == TenderStatus.EVALUATED || t.getStatus() == TenderStatus.AWARDED)
                .collect(Collectors.toList());

        if (dateFrom != null) {
            completed = completed.stream()
                    .filter(t -> t.getEvaluationCompletedDate() != null
                    && !t.getEvaluationCompletedDate().toLocalDate().isBefore(dateFrom))
                    .collect(Collectors.toList());
        }

        if (dateTo != null) {
            completed = completed.stream()
                    .filter(t -> t.getEvaluationCompletedDate() != null
                    && !t.getEvaluationCompletedDate().toLocalDate().isAfter(dateTo))
                    .collect(Collectors.toList());
        }

        List<Map<String, Object>> evaluationReportList = new ArrayList<>();
        for (Tender tender : completed) {
            Map<String, Object> eval = new HashMap<>();
            eval.put("tenderReference", tender.getReferenceNumber());
            eval.put("tenderTitle", tender.getTitle());
            eval.put("bidsEvaluated", tender.getBidCount());
            eval.put("evaluatorCount", tender.getEvaluatorsSubmittedCount());
            eval.put("evaluationDate", tender.getEvaluationCompletedDate());
            eval.put("winningScore", tender.getWinningBidScoreFormatted());
            evaluationReportList.add(eval);
        }

        req.setAttribute("evaluationReportList", evaluationReportList);
    }

    private void handleCsvExport(HttpServletRequest req, HttpServletResponse resp,
            String tab, LocalDate dateFrom, LocalDate dateTo,
            String statusParam, String categoryParam, String keyword) throws IOException {
        
        resp.setContentType("text/csv");
        resp.setHeader("Content-Disposition", "attachment; filename=\"report_" + tab + "_" + 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".csv\"");

        java.io.PrintWriter out = resp.getWriter();
        
        // Write report header
        out.println("# Report Type: " + getTabDisplayName(tab));
        out.println("# Generated: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        out.println("# Generated By: " + ((User) req.getSession().getAttribute("user")).getFullName());
        
        if (dateFrom != null) {
            out.println("# Date From: " + dateFrom);
        }
        if (dateTo != null) {
            out.println("# Date To: " + dateTo);
        }
        out.println();
        
        switch (tab) {
            case "summary":
                exportSummaryToCsv(req, out);
                break;
            case "tenders":
                exportTendersToCsv(req, out);
                break;
            case "awards":
                exportAwardsToCsv(req, out);
                break;
            case "suppliers":
                exportSuppliersToCsv(req, out);
                break;
            case "evaluation":
                exportEvaluationToCsv(req, out);
                break;
        }
        
        out.flush();
    }

    private String getTabDisplayName(String tab) {
        switch (tab) {
            case "summary": return "Summary Report";
            case "tenders": return "Tender Report";
            case "awards": return "Awards Report";
            case "suppliers": return "Supplier Report";
            case "evaluation": return "Evaluation Report";
            default: return "Report";
        }
    }

    private void exportSummaryToCsv(HttpServletRequest req, java.io.PrintWriter out) {
        Map<String, Object> stats = (Map<String, Object>) req.getAttribute("summaryStats");
        List<Map<String, Object>> categoryStats = (List<Map<String, Object>>) req.getAttribute("categoryStats");
        List<Map<String, Object>> monthlyStats = (List<Map<String, Object>>) req.getAttribute("monthlyStats");
        
        out.println("SUMMARY STATISTICS");
        out.println("Total Tenders," + stats.get("totalTenders"));
        out.println("Open Tenders," + stats.get("openTenders"));
        out.println("Awarded Tenders," + stats.get("awardedTenders"));
        out.println("Total Award Value," + stats.get("totalAwardValue"));
        out.println("Total Bids Received," + stats.get("totalBids"));
        out.println("Registered Suppliers," + stats.get("registeredSuppliers"));
        out.println("Pending Evaluations," + stats.get("pendingEvaluations"));
        out.println("Avg Bids per Tender," + stats.get("avgBidsPerTender"));
        out.println();
        
        out.println("TENDERS BY CATEGORY");
        out.println("Category,Total Tenders,Open,Awarded,Total Value");
        if (categoryStats != null) {
            for (Map<String, Object> cat : categoryStats) {
                out.println(cat.get("category") + "," + 
                           cat.get("totalTenders") + "," + 
                           cat.get("openTenders") + "," + 
                           cat.get("awardedTenders") + "," + 
                           cat.get("totalValue"));
            }
        }
        out.println();
        
        out.println("MONTHLY TRENDS");
        out.println("Month,Tenders Published,Bids Received,Awards Made,Award Value");
        if (monthlyStats != null) {
            for (Map<String, Object> month : monthlyStats) {
                out.println(month.get("month") + "," + 
                           month.get("tendersPublished") + "," + 
                           month.get("bidsReceived") + "," + 
                           month.get("awardsMade") + "," + 
                           month.get("awardValue"));
            }
        }
    }

    private void exportTendersToCsv(HttpServletRequest req, java.io.PrintWriter out) {
        List<Tender> tenders = (List<Tender>) req.getAttribute("tenderReportList");
        
        out.println("TENDER REPORT");
        out.println("Reference,Title,Category,Status,Published Date,Closing Date,Bids,Estimated Value,Awarded Value");
        
        if (tenders != null) {
            for (Tender tender : tenders) {
                out.println("\"" + tender.getReferenceNumber() + "\"," +
                           "\"" + tender.getTitle() + "\"," +
                           tender.getCategory().getDisplayName() + "," +
                           tender.getStatus().getDisplayName() + "," +
                           (tender.getCreatedAt() != null ? tender.getCreatedAt().toLocalDate() : "") + "," +
                           (tender.getClosingDateTime() != null ? tender.getClosingDateTime().toLocalDate() : "") + "," +
                           tender.getBidCount() + "," +
                           tender.getFormattedEstimatedValue() + "," +
                           tender.getFormattedAwardedValue());
            }
        }
    }

    private void exportAwardsToCsv(HttpServletRequest req, java.io.PrintWriter out) {
        List<Map<String, Object>> awards = (List<Map<String, Object>>) req.getAttribute("awardReportList");
        Map<String, Object> stats = (Map<String, Object>) req.getAttribute("awardStats");
        
        out.println("AWARDS SUMMARY");
        out.println("Total Awards," + stats.get("totalAwards"));
        out.println("Total Award Value," + stats.get("totalValue"));
        out.println("Average Award Value," + stats.get("avgAwardValue"));
        out.println();
        
        out.println("AWARD DETAILS");
        out.println("Tender Reference,Tender Title,Category,Winning Supplier,Award Date,Estimated Value,Awarded Value,Variance");
        
        if (awards != null) {
            for (Map<String, Object> award : awards) {
                out.println("\"" + award.get("tenderReference") + "\"," +
                           "\"" + award.get("tenderTitle") + "\"," +
                           award.get("category") + "," +
                           "\"" + award.get("supplierName") + "\"," +
                           (award.get("awardDate") != null ? award.get("awardDate") : "") + "," +
                           award.get("formattedEstimatedValue") + "," +
                           award.get("formattedAwardedValue") + "," +
                           award.get("formattedVariance"));
            }
        }
    }

    private void exportSuppliersToCsv(HttpServletRequest req, java.io.PrintWriter out) {
        List<Map<String, Object>> suppliers = (List<Map<String, Object>>) req.getAttribute("supplierReportList");
        
        out.println("SUPPLIER REPORT");
        out.println("Registration #,Company Name,Email,Contact,Registered Date,Bids Submitted,Awards Won,Total Award Value");
        
        if (suppliers != null) {
            for (Map<String, Object> supplier : suppliers) {
                out.println("\"" + supplier.get("registrationNumber") + "\"," +
                           "\"" + supplier.get("fullName") + "\"," +
                           supplier.get("email") + "," +
                           supplier.get("contactNumber") + "," +
                           (supplier.get("createdAt") != null ? supplier.get("createdAt") : "") + "," +
                           supplier.get("bidsSubmitted") + "," +
                           supplier.get("awardsWon") + "," +
                           supplier.get("formattedTotalAwardValue"));
            }
        }
    }

    private void exportEvaluationToCsv(HttpServletRequest req, java.io.PrintWriter out) {
        List<Map<String, Object>> evaluators = (List<Map<String, Object>>) req.getAttribute("evaluatorStats");
        List<Map<String, Object>> evaluations = (List<Map<String, Object>>) req.getAttribute("evaluationReportList");
        
        out.println("EVALUATOR PERFORMANCE");
        out.println("Evaluator Name,Evaluations Completed,Total Bids Scored,Average Score Given,Last Evaluation Date");
        
        if (evaluators != null) {
            for (Map<String, Object> evaluator : evaluators) {
                out.println("\"" + evaluator.get("name") + "\"," +
                           evaluator.get("evaluationsCompleted") + "," +
                           evaluator.get("totalBidsScored") + "," +
                           evaluator.get("avgScoreGiven") + "," +
                           (evaluator.get("lastEvaluationDate") != null ? evaluator.get("lastEvaluationDate") : ""));
            }
        }
        
        out.println();
        out.println("COMPLETED EVALUATIONS");
        out.println("Tender Reference,Tender Title,Bids Evaluated,Evaluators,Evaluation Date,Winning Score");
        
        if (evaluations != null) {
            for (Map<String, Object> eval : evaluations) {
                out.println("\"" + eval.get("tenderReference") + "\"," +
                           "\"" + eval.get("tenderTitle") + "\"," +
                           eval.get("bidsEvaluated") + "," +
                           eval.get("evaluatorCount") + "," +
                           (eval.get("evaluationDate") != null ? eval.get("evaluationDate") : "") + "," +
                           eval.get("winningScore"));
            }
        }
    }
}