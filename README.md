
## Customer Purchasing Service :: Rewards

### Goal
    Build a BE service to manage Customer Rewards for a retailer.
    The initial logic calculates the reward points earned by a set of customers.
    For a 3 month period, the customer's monthly rewards and total rewards are calculated.

    Rewards forumla : A customer receives 2 points for every dollar spent over $100,
                      plus 1 point for every dollar spent over $50 for each transaction.

    Note: A rough grab of the last 3 months is used in this service.
          So, just a range of 3 months, represented as integers ( one-based for January ),
          is used to designate the months selected for the calculation.
          An exact 90-day range is not calcuated here.
          So currently, in May, the last 3 months include March, Apr, May - in leiu today's current date.
          A more fine-grained service can be developed to calculate rewards from 
          Mar 12, 2022 - May 12, 2022, being that today is the 12th.

### Tech Stack
    The service is built using Java 12, SpringBoot 2.6.7.
    Transaction data is loaded in-memory using H2 DB.
    Hibernate JPA and ExecutorService are also applied.
    REST endpoints are available to invoke the service resources, 
    specifically transactions can be added to a customer's history.


### Build
`cd CustRewards`

`mvn package`

 `java -jar target/*.jar`


### Usage
    See http://localhost:8080/customer-purchasing/swagger-ui/index.html

    Using a Postman client:
        Request All customer rewards list for last 3 months:
           GET http://localhost:8080/customer-purchasing/rewards
    
        Request customer rewards list for specific customers (last 3 months):
           GET http://localhost:8080/customer-purchasing/rewards?customerIds=1,2

        Request customer rewards list with bounding months:
           GET http://localhost:8080/customer-purchasing/rewards?customerIds=1,2&startMonth=1&endMonth=3
    
        Get customer transactions:
            GET localhost:8080/customer-purchasing/transactions/{customerId}
        
        Add customer transaction:
           POST http://localhost:8080/customer-purchasing/transactions/create
           {
             "customerId" : 1,
             "date" : "05-05-2022 00:00:00",
             "cost" : 87.00
           }
    
        Clear all customer transactions:
           POST http://localhost:8080/customer-purchasing//transactions/clearAll

### Data Structures
    Dates are stored without TimeZone detail. Further development will require that
    transaction dates include the TimeZone where they were transacted.

    Data structures:
        Transaction {
            Integer id;
            Long customerId;
            LocalDateTime date;
            BigDecimal cost;
        }
        CustomerRewards {
            private Long customerId;
            Map<String, Integer> monthlyRewards;
            Integer grandTotal = 0;
        }

    
### Test Philosophy
    Integration and unit tests are run by Maven.
    The integration test is a high-level test that loads the full SpringBoot context.
    This facilitates development as the SpringBoot instance is further configured.

    Unit tests are run to verify the validation and rewards calculation logic.
    Unit tests should avoid exercising pure SpringBoot infrastructure code such as JPA repositories or Pojos since this functionality is industry standard and deployed world-wide.

    Tests to exercise the Calculate Rewards Controller can be developed using a Postman suite.