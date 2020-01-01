package com.spring.tutorial.mongo;

import com.spring.tutorial.mongo.data.Person;
import com.spring.tutorial.mongo.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

@SpringBootApplication
public class Application  implements CommandLineRunner {

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        personRepository.deleteAll();

        // Insert using Spring Data
        personRepository.save(new Person("Bored", "Knuth"));
        personRepository.save(new Person("Excited","Tesla"));
        personRepository.save(new Person("Intelligent", "Alvarez"));
        personRepository.save(new Person("Happy","Avogadro"));

        fetchData();

        System.out.println(personRepository.findByLastName("Avogadro"));

        // insert using MongoTemplate
        mongoTemplate.insert(new Person("Sad", "Robert"));
        mongoTemplate.insert(new Person("Smart", "Hendricks"));
        mongoTemplate.insert(new Person("Famous", "Martin"));
        mongoTemplate.insert(new Person("Famous", "Martin"));

        fetchData();

        // Update
        Person person = mongoTemplate.findOne(Query.query(Criteria.where("firstName").is("Sad")), Person.class);
        person.setFirstName("Awesome");
        mongoTemplate.save(person);

        Person updatedPerson = mongoTemplate.findOne(Query.query(Criteria.where("firstName").is("Awesome")), Person.class);
        System.out.println(updatedPerson);

        // Update multiple
        Query query1 = new Query();
        query1.addCriteria(Criteria.where("firstName").is("Famous"));
        Update update1 = new Update();
        update1.set("firstName", "Victor");
        mongoTemplate.updateMulti(query1, update1, Person.class);

        fetchData();

        // Upsert (if it does not exist then create it)
        Query query2 = new Query();
        query2.addCriteria(Criteria.where("firstName").is("Markus"));
        Update update2 = new Update();
        update2.set("firstName", "Nick");
        mongoTemplate.upsert(query2, update2, Person.class);

        fetchData();

        mongoTemplate.remove(query2, Person.class);

        fetchData();
    }

    private void fetchData() {
        for(Person person: personRepository.findAll()){
            System.out.println(person);
        }
    }


}
