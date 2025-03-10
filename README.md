###Assumptions
- Assuming the prices of products are defined in original currency
- First step: apply discount as per conditions
- Second step: convert the discounted amount to USD (to meet the condition: For every $100 on the bill, there is a $5 discount)
- Third step: convert USD to target currency and return the response from API
- Assuming there are three types of Users making the purchase: affiliate, employee and customer
- Customer is considered loyal if the tenure is greater than 24 months (2 years)

- Implemented basic authentication: 
  - username: user 
  - password: password
- Implemented caching to avoid multiple hits to the "https://open.er-api.com" API
- The API that is implemented is exposed on URL: http://localhost:8080/api/calculate

###Post request
curl -X POST -H "Content-Type: application/json" -u user:password -d "{ \"items\": [ { \"name\": \"Eggs\", \"price\": 12.56, \"quantity\": 1, \"isGrocery\": true }, { \"name\": \"Toothpaste\", \"price\": 20.45, \"quantity\": 2, \"isGrocery\": false } ], \"totalAmount\": 53.46, \"userType\": \"affiliate\", \"customerTenure\": 25, \"originalCurrency\": \"AED\", \"targetCurrency\": \"USD\" }" http://localhost:8080/api/calculate

###How to run application on Windows
- set MAVEN_OPTS=--add-opens java.base/java.lang=ALL-UNNAMED
- mvn spring-boot:run
