# AWS PoC
Proof of Concept project used to test S3 & Minio integration using AWS Java SDK.

## Used solution
AWS SDK for Java provided via Maven repo.

## Configuration
Use following environment variables to configure app:

| Variable name        | Description                                                  | Example value       |
| -------------------- |:------------------------------------------------------------:|--------------------:|
|AWS_ACCESS_KEY_ID     | Access key for AWS or Minio                                  | `12345678922`       |
|AWS_SECRET_ACCESS_KEY | AWS or Minio Secret for provided key                         | `63791234123`       |
|AWS_REGION            | AWS Region where bucket is located. Less important for Minio | `us-east-1`         |
|AWS_ENDPOINT          | Used for Minio only - address to Minio instance              | `http://minio:9000` |

### AWS S3
AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY and AWS_REGION are required to run against AWS S3.

### Minio
All env variables should be defined to use Minio.

## External resources
- AWS S3: https://aws.amazon.com/s3/
- Minio: https://minio.io/
- AWS Java SDK: http://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/welcome.html