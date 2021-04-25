# README

## Instructions

### Prerequisites

- Install Docker
- Install Maven
- Install Java 15
- Start a local Docker repository: `docker run -d -p 5000:5000 --restart=always --name registry registry:2`

### Compile

```shell
    mvn clean compile com.google.cloud.tools:jib-maven-plugin:3.0.0:dockerBuild
```

### Run the app by using an IDE Like Intellij

If you are using Intellij just install all Maven dependencies and press the Play button to run the application.

### Start the application With Docker

Just run a terminal session, go to the project folder and run:

```shell
docker-compose up -d
```

## Considerations

The requirements could be understood in different ways, and I think this was part of the test. In my opinion the
checkout event should be separated from the "change quantity event".

Change quantity could be refined even further in:

- add a product;
- remove a product;
- change quantity;

Product removal should be triggered by the total absence of a SKU in the following body or by the quantity set to 0,
just for the sake of having some flexibility.

```json
{
  "products": {
    "044d2518-4ed0-4055-9821-e2608f6cf2e5": 110,
    "a70931cc-d7f3-4286-9ef0-c63fdeec3cb2": 200
  },
  "username": "foo"
}
```

I introduced a validation on the products so that products that are not in the products table cannot be added to the
cart. The validation could be extended even further to check if a product is in stock.

Using CQRS, furthermore, it is not responsibility of the "checkout phase" endpoint to return the total price. So in my
opinion the "checkout event" should only set a flag that "freezes" the cart waiting for the client to pay. For this
reason I have created one more endpoint which can be called separately in order to retrieve the total price of the
active cart before a checkout.

Having more time I would have plaid a bit with JMS and ActiveMQ, but it was overkill for the purpose of the test.

For the sake of the demo the username comes from the path in the request or from within the post object, obviously in a
real scenario this would be inside a bearer token in the *Authorization* header, for further reference:
https://leanpub.com/oauth2-ely5

The username is used in order to get the active cart. A user has always one cart in the ACTIVE state. When the cart is
checked out this won't be deleted, it will be only flagged as checked out. This way we can extract important information
from the carts: what kind of products a user likes, what his average spending is, etc..

As a final note the app was developed trying to follow DDD methodology.

The application will automatically create on load a test user and some demo products to start playing.

## API Description

The application is available at http://localhost:8888, so I assume this is the base of my address.  <br/>

### Change quantity

**Endpoint path**: `/cart/changequantity` <br/>
**Successful Response**: 200  <br/>
**Request body**:  <br/>

```json
{
  "products": {
    "044d2518-4ed0-4055-9821-e2608f6cf2e5": 110,
    "a70931cc-d7f3-4286-9ef0-c63fdeec3cb2": 200
  },
  "username": "foo"
}
```

### Checkout

**Endpoint path**: `/cart/checkout`  <br/>
**Successful Response**: 200  <br/>
**Request body**:  <br/>

```json
{
  "username": "foo"
}
```

### Get total price

**Endpoint path**: `/cart/<username>/getcartinfo`  <br/>
**Successful Response**: 200  <br/>
**Request body**:  <br/>

```json
{
  "userId": 1,
  "cartId": 1,
  "cartStatus": "ACTIVE",
  "totalFinalPrice": 19700.0,
  "products": [
    {
      "productId": 2,
      "productSku": "3779ebe8-cebd-438f-8399-59228739ed48",
      "minItemsForDiscount": 200,
      "numberOfFreeItems": 3,
      "quantity": 200,
      "stockQuantity": 400,
      "unitPrice": 100.0,
      "productName": "Rolex"
    }
  ]
}
```



