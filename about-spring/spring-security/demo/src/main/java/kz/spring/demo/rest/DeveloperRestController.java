package kz.spring.demo.rest;


import kz.spring.demo.model.Developer;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("api/v1/developers")
public class DeveloperRestController {

    private static CopyOnWriteArrayList<Developer> developers = Stream.of(
            new Developer(1L, "name1", "surname1"),
            new Developer(2L, "name2", "surname2"),
            new Developer(3L, "name3", "surname3")
    ).collect(Collectors.toCollection(CopyOnWriteArrayList::new));

    @GetMapping
    @PreAuthorize("hasAuthority('developers:read')")
    public List<Developer> getAll() {
        return developers;
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('developers:write')")
    public Developer getById(@PathVariable Long id) {
        return developers.stream().filter(x -> x.getId() == id).findFirst().orElse(null);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('developers:write')")
    public Developer create(@RequestBody Developer developer) {
        developers.add(developer);
        return developer;
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('developers:write')")
    public List<Developer> deleteDeveloper(@PathVariable Long id) {
        System.out.println(id);
        System.out.println(developers.stream().filter(x -> x.getId() == id).findFirst());
        developers.removeIf(x -> x.getId() == id);
        return developers;
    }

}
