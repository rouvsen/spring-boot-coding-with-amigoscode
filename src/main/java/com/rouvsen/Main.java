package com.rouvsen;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@SpringBootApplication
//@ComponentScan(basePackages = "com.rouvsen")
//@EnableAutoConfiguration
//@Configuration
@RestController
@RequestMapping("api/v1/customers")
public class Main {

    private final CustomerRepository customerRepository;

    public Main(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @GetMapping
    public List<Customer> getCustomer(){
        return customerRepository.findAll();
    }

    record NewCustomerRequest(
            String name,
            String email,
            Integer age
    ){}

    @PostMapping
    public Customer saveCustomer(@RequestBody NewCustomerRequest request){
        Customer customer = new Customer();
        customer.setName(request.name);
        customer.setAge(request.age);
        customer.setEmail(request.email);
        return customerRepository.save(customer);
    }

    @GetMapping("/greet")
    public GreetResponse greet(){
        GreetResponse response = new GreetResponse(
                "Hello",
                List.of("Java", "JavaScript", "Rust", "Python"),
                new Person("Rovshan", 21, 21_000d)
        );
        return response;
    }

    @DeleteMapping("/{customerId}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable("customerId") Integer id){
        Optional<Customer> customerOp = customerRepository.findById(id);

        if (!customerOp.isPresent()){
            return ResponseEntity.notFound().build();
        }

        customerRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    //Exercise for me (by AmigosCode). We need api for Updating
    @PatchMapping("/{customerId}")
    public ResponseEntity<Customer> updateCustomer(
            @RequestBody Customer customerDetails,
            @PathVariable("customerId") Integer id
    ){
        return customerRepository.findById(id)
                .map(customer -> {
                    if (customerDetails.getName() != null){
                        customer.setName(customerDetails.getName());
                    }
                    if (customerDetails.getEmail() != null){
                        customer.setEmail(customerDetails.getEmail());
                    }
                    if (customerDetails.getAge() != null){
                        customer.setAge(customerDetails.getAge());
                    }
                    Customer updatedCustomer = customerRepository.save(customer);
                    return ResponseEntity.ok(updatedCustomer);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    record Person(String name, Integer age, Double savings){

    }

    record GreetResponse(  //immutable class
            String greet,
            List<String> favProgrammingLanguages,
            Person person
    ){

    }

    //alternative of record
//    class GreetResponse{
//        private final String greet;
//
//        GreetResponse(String greet) {
//        this.greet = greet;
//        }
//
//        public String getGreet(){
//            return this.greet;
//        }
//
//        @Override
//        public boolean equals(Object o) {
//            if (this == o) return true;
//            if (o == null || getClass() != o.getClass()) return false;
//            GreetResponse that = (GreetResponse) o;
//            return Objects.equals(greet, that.greet);
//        }
//
//        @Override
//        public int hashCode() {
//            return Objects.hash(greet);
//        }
//
//        @Override
//        public String toString() {
//            return "GreetResponse{greeting='%s'}".formatted(greet);
//        }
//    }
}
