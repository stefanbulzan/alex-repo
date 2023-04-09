package com.alex.service;

import com.alex.exceptions.NotFoundException;
import com.alex.exceptions.ValidationException;
import com.alex.model.InvoiceFilter;
import com.alex.model.dto.InvoiceDto;
import com.alex.model.entity.Invoice;
import com.alex.repository.InvoiceRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class InvoiceService {
    private final InvoiceRepository invoiceRepository;
    private final EntityManager em;

    public InvoiceService(InvoiceRepository invoiceRepository, EntityManager em) {
        this.invoiceRepository = invoiceRepository;
        this.em = em;
    }

    public List<Invoice> getAll(InvoiceFilter invoiceFilter) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Invoice> cr = cb.createQuery(Invoice.class);
        Root<Invoice> invoice = cr.from(Invoice.class);
        cr.select(invoice);
        List<Predicate> predicates = filterToPredicates(cb, invoice, invoiceFilter);
        cr.where(predicates.toArray(new Predicate[0]));
        TypedQuery<Invoice> query = em.createQuery(cr);
        return query.getResultList();
    }

    private List<Predicate> filterToPredicates(CriteriaBuilder cb, Root<Invoice> invoice, InvoiceFilter invoiceFilter) {
        List<Predicate> predicates = new ArrayList<>();
        if (invoiceFilter.companyName() != null) {
            predicates.add(cb.equal(invoice.get("companyName"), invoiceFilter.companyName()));
        }
        if (invoiceFilter.cui() != null) {
            predicates.add(cb.equal(invoice.get("cui"), invoiceFilter.cui()));
        }
        if (invoiceFilter.minAmount() != null) {
            predicates.add(cb.greaterThan(invoice.get("invoiceTotal"), invoiceFilter.minAmount()));
        }
        if (invoiceFilter.maxAmount() != null) {
            predicates.add(cb.lessThan(invoice.get("invoiceTotal"), invoiceFilter.maxAmount()));
        }
        return predicates;
    }

    public Invoice addInvoice(InvoiceDto request) {
        validateInvoice(request);
        return invoiceRepository.save(new Invoice(request.companyName(), request.cui(), request.invoiceTotal()));
    }

    private void validateInvoice(@NotNull InvoiceDto request) {
        if (request.companyName() == null || request.companyName().isBlank() || request.companyName().length() < 2) {
            throw new ValidationException("Please provide company name");
        }
    }

    private void validateInvoiceExists(Integer id) {
        if (!invoiceRepository.existsById(id))
            throw new NotFoundException("The invoice with id " + id + " does not exist");
    }

    public void deleteInvoice(Integer id) {
        validateInvoiceExists(id);
        invoiceRepository.deleteById(id);
    }

    public Invoice updateInvoice(InvoiceDto updateRequest, Integer id) {
        validateInvoiceExists(id);
        return invoiceRepository.findById(id)
                .map(invoice -> {
                    invoice.setCompanyName(updateRequest.companyName());
                    invoice.setCui(updateRequest.cui());
                    invoice.setInvoiceTotal(updateRequest.invoiceTotal());
                    return invoice;
                })
                .map(invoiceRepository::save)
                .orElse(null);
    }


    public void deleteAllInvoices() {
        invoiceRepository.deleteAll();
    }

    public Optional<Invoice> getById(Integer id) {
        return invoiceRepository.findById(id);
    }
}
