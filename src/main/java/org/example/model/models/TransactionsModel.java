package org.example.model.models;

import org.example.SessionManager;
import org.example.model.database.entity.Transaction;
import org.example.model.database.service.TransactionService;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;

public class TransactionsModel {

    private final TransactionService transactionService = new TransactionService();

    public record ImportResult(int imported, int failed) {}

    public ImportResult importFromXml(File file) throws Exception {
        Document doc = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder().parse(file);
        NodeList nodes = doc.getElementsByTagName("transaction");
        int imported = 0, failed = 0;
        int companyId = SessionManager.getInstance().getCurrentCompanyId();

        for (int i = 0; i < nodes.getLength(); i++) {
            try {
                Element el = (Element) nodes.item(i);
                Transaction t = new Transaction(
                        companyId,
                        Integer.parseInt(getTag(el, "accountId")),
                        parseNullableInt(getTag(el, "projectId")),
                        parseNullableInt(getTag(el, "clientId")),
                        getTag(el, "type"),
                        new BigDecimal(getTag(el, "amount")),
                        getTag(el, "description"),
                        LocalDate.parse(getTag(el, "date"))
                );
                transactionService.addTransaction(t);
                imported++;
            } catch (Exception e) {
                    failed++;
                    System.err.println("Failed to import transaction #" + i + ": " + e.getMessage());
                    e.printStackTrace();
                }
        }
        return new ImportResult(imported, failed);
    }

    private String getTag(Element el, String tag) {
        NodeList list = el.getElementsByTagName(tag);
        return list.getLength() > 0 ? list.item(0).getTextContent().trim() : "";
    }

    private Integer parseNullableInt(String s) {
        return (s == null || s.isBlank()) ? null : Integer.parseInt(s);
    }
}