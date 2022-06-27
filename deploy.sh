#!/bin/bash

docker build -t localhost:5000/hi-authentication .

docker push localhost:5000/hi-authentication

kubectl delete -f k8s/*

kubectl apply -f k8s/*

