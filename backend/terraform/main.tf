provider "aws" {
  region = "eu-west-1"
}

module "eks" {
  source          = "terraform-aws-modules/eks/aws"
  cluster_name    = "student-cluster"
  cluster_version = "1.29"
  subnet_ids      = ["subnet-1","subnet-2"]
  vpc_id          = "vpc-xxxx"
}
