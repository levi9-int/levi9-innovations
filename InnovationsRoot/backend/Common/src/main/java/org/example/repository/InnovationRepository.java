package org.example.repository;

import org.example.dto.InnovationRequest;
import org.example.model.Innovation;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

//@Repository
//public interface InnovationRepository extends CrudRepository{
//
//    private List<Innovation> innovations = Stream.of(
//            new Innovation(1, "Inn1", "Desc1"),
//            new Innovation(2, "Inn2", "Desc1"),
//            new Innovation(3, "Inn3", "Desc1"),
//            new Innovation(4, "Inn4", "Desc1")
//    ).collect(Collectors.toList());
//
//    public List<Innovation> getInnovations() {
//        return innovations;
//    }
//
//    public void addInnovation(Innovation newInnovation) {
//
//        newInnovation.setId(innovations.size() + 1);
//        innovations.add(newInnovation);
//    }
//}
