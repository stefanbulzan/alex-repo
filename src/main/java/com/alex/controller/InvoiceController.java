package com.alex.controller;

import com.alex.model.InvoiceFilter;
import com.alex.model.dto.InvoiceDto;
import com.alex.model.entity.Invoice;
import com.alex.model.mappers.InvoiceMapper;
import com.alex.service.InvoiceService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.*;

@RestController
@RequestMapping("invoices")
public class InvoiceController {
    private final InvoiceService service;
    private final InvoiceMapper mapper;

    public InvoiceController(InvoiceService service, InvoiceMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping
    public List<InvoiceDto> getInvoice(InvoiceFilter invoiceFilter) {
        return service.getAll(invoiceFilter).stream()
                .map(mapper::toDto)
                .toList();
    }

    @GetMapping("{invoiceId}")
    public Invoice getInvoiceById(@PathVariable("invoiceId") Integer id) {
        return service.getById(id).orElse(null);
    }


    @PostMapping
    public Invoice addInvoice(@RequestBody InvoiceDto request) {
        return service.addInvoice(request);
    }


    @DeleteMapping("{invoiceId}")
    public List<InvoiceDto> deleteInvoice(@PathVariable("invoiceId") Integer id) {
        service.deleteInvoice(id);
        return null;
    }

    @DeleteMapping
    public String deleteAllInvoices() {
        service.deleteAllInvoices();
        return "ALL INVOICES DELETED";
    }

    @PutMapping("{invoiceId}")
    public Invoice updateInvoice(@PathVariable("invoiceId") Integer id, @RequestBody InvoiceDto updateRequest) {
        return service.updateInvoice(updateRequest, id);
    }

    public static void main(String[] args) {
        List<String> names = List.of("Ana", "Ioan", "Pascal");
        List<String> list = names.stream()
                .map(name -> name + "a")
                .flatMap(name -> IntStream.range(0, name.length())
                        .mapToObj(name::charAt)
                        .map(String::valueOf))
                .toList();
//        System.out.println(list);

        List<Person> people = List.of(
                new Person("Ana", 11, "Oradea"),
                new Person("Zidane", 31, "Cluj"),
                new Person("Mihai", 31, "Oradea"),
                new Person("Dorin", 14, "Bucuresti"),
                new Person("Angela", 35, "Bucuresti")
        );

        var collect = people.stream()
                .collect(groupingBy(Person::oras, mapping(Person::name, toList())));
        System.out.println(collect);

    }
}

record Person(String name, int age, String oras) {
}
