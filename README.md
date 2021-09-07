# Welcome
This project is a spike to aggregate messages from SQS into batch of messages using spring batch (chuck processing) batch.  
Here is the stackover flow [link](https://stackoverflow.com/questions/67793048/message-aggregation-using-sqs-and-springboot) where i posted this query.

# How to run locally
Project is built based on spring boot and gradle, for connecting to SQS i am using `STSAssumeRoleSessionCredentialsProvider` based approach, and the below enviornment variables are required.
- `SQS_URL` - The Queue on which you are going to read the mesages from
- `REGION` - The Region of the Queue
- `ROLE_ARN` - The ROLE ARN with STS Assume role capability.
- `QUEUE_NAME` - The Queue Name
