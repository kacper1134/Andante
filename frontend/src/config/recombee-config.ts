import recombee from "recombee-js-api-client";

const recombeeClient = new recombee.ApiClient(
  process.env.REACT_APP_RECOMBEE_DATABASE_ID,
  process.env.REACT_APP_RECOMBEE_PUBLIC_KEY,
  { 'region': 'eu-west' }
);

export default recombeeClient;
