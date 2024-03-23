const express = require("express");
const app = express();
// This is your test secret API key.
const stripe = require("stripe")('sk_test_51OGbbcKmvrHpopBQJs92CJtEDpbopR4vP9t6LT4bwJpZScVhbcPnSS60hcoY2lFKTrSEfQsFy8FNmnh7nJ0SerCZ00qqeNyKFY');

app.use(express.static("public"));
app.use(express.json());

const calculateOrderAmount = (items) => {
  //use zar later on please
  return 5000;
};

app.post("/create-payment-intent", async (req, res) => {
  const { items } = req.body;


  const paymentIntent = await stripe.paymentIntents.create({
    amount: calculateOrderAmount(items),
    currency: "usd",
    // In the latest version of the API, specifying the `automatic_payment_methods` parameter is optional because Stripe enables its functionality by default.
    automatic_payment_methods: {
      enabled: true,
    },
  });

  res.send({
    clientSecret: paymentIntent.client_secret,
  });
});


app.listen(4242, () => console.log("Node server listening on port 4242!"));