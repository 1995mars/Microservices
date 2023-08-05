### Automatic self-healing, scaling, deployments inside microservices network using Kubernetes
---

**Description:** This repository has six maven projects with the names **accounts, loans, cards, configserver, eurekaserver, gatewayserver** which are continuation from the 
section12 repository. All these microservices will be leveraged to explore the automatic self-healing, scaling, deployments inside microservices network using **Kubernetes**.
Below are the key steps that we will be following in this section13 repository,

**Key steps:**
- Like we discussed in the course, create a **GCP account and kubernetes cluster**.
- Like we discussed in the course, connect to the kubernetes cluster using **gcloud** command and issue various commands to it using **kubectl**
- Like we discussed in the course, create the below 5 yaml files to get started with deployment of microservices into kubernetes cluster
### \accounts\kubernetes\1_configmaps.yml
```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: eazybank-configmap
data:
  # SPRING_ZIPKIN_BASEURL: http://zipkin-service:9411/
  MANAGEMENT_ZIPKIN_TRACING_ENDPOINT: http://zipkin-service:9411/api/v2/spans
  SPRING_PROFILES_ACTIVE: prod
  SPRING_CONFIG_IMPORT: configserver:http://configserver-service:8071/
  EUREKA_CLIENT_SERVICEURL_DEFAULTZONE: http://eurekaserver-service:8070/eureka/
  EUREKA_APPLICATION_NAME: eurekaserver
  ACCOUNTS_APPLICATION_NAME: accounts
```
### \accounts\kubernetes\2_zipkin.yml
```yaml
  apiVersion: apps/v1
  kind: Deployment
  metadata:
    name: zipkin-deployment
    labels:
      app: zipkin
  spec:
    replicas: 1
    selector:
      matchLabels:
        app: zipkin
    template:
      metadata:
        labels:
          app: zipkin
      spec:
        containers:
        - name: zipkin
          image: openzipkin/zipkin
          ports:
          - containerPort: 9411
  ---
  apiVersion: v1
  kind: Service
  metadata:
    name: zipkin-service
  spec:
    selector:
      app: zipkin
    type: LoadBalancer
    ports:
      - protocol: TCP
        port: 9411
        targetPort: 9411
```
### \accounts\kubernetes\3_configserver.yml
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: configserver-deployment
  labels:
    app: configserver
spec:
  replicas: 1
  selector:
    matchLabels:
      app: configserver
  template:
    metadata:
      labels:
        app: configserver
    spec:
      containers:
      - name: configserver
        image: eazybytes/configserver:latest
        ports:
        - containerPort: 8071
        env:
        - name: MANAGEMENT_ZIPKIN_TRACING_ENDPOINT
          valueFrom: 
            configMapKeyRef:
              name: eazybank-configmap
              key: MANAGEMENT_ZIPKIN_TRACING_ENDPOINT
        - name: SPRING_PROFILES_ACTIVE
          valueFrom: 
            configMapKeyRef:
              name: eazybank-configmap
              key: SPRING_PROFILES_ACTIVE
---
apiVersion: v1
kind: Service
metadata:
  name: configserver-service
spec:
  selector:
    app: configserver
  type: LoadBalancer
  ports:
    - protocol: TCP
      port: 8071
      targetPort: 8071
```
### \accounts\kubernetes\4_eurekaserver.yml
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: eurekaserver-deployment
  labels:
    app: eurekaserver
spec:
  replicas: 1
  selector:
    matchLabels:
      app: eurekaserver
  template:
    metadata:
      labels:
        app: eurekaserver
    spec:
      containers:
      - name: eurekaserver
        image: eazybytes/eurekaserver:latest
        ports:
        - containerPort: 8070
        env:
        - name: SPRING_APPLICATION_NAME
          valueFrom:
            configMapKeyRef:
              name: eazybank-configmap
              key: EUREKA_APPLICATION_NAME
        - name: SPRING_PROFILES_ACTIVE
          valueFrom: 
            configMapKeyRef:
              name: eazybank-configmap
              key: SPRING_PROFILES_ACTIVE
        - name: MANAGEMENT_ZIPKIN_TRACING_ENDPOINT
          valueFrom: 
            configMapKeyRef:
              name: eazybank-configmap
              key: MANAGEMENT_ZIPKIN_TRACING_ENDPOINT
        - name: SPRING_CONFIG_IMPORT
          valueFrom: 
            configMapKeyRef:
              name: eazybank-configmap
              key: SPRING_CONFIG_IMPORT

---
apiVersion: v1
kind: Service
metadata:
  name: eurekaserver-service
spec:
  selector:
    app: eurekaserver
  type: LoadBalancer
  ports:
    - protocol: TCP
      port: 8070
      targetPort: 8070
```
### \accounts\kubernetes\5_accounts.yml
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: accounts-deployment
  labels:
    app: accounts
spec:
  replicas: 1
  selector:
    matchLabels:
      app: accounts
  template:
    metadata:
      labels:
        app: accounts
    spec:
      containers:
      - name: accounts
        image: eazybytes/accounts:latest
        ports:
        - containerPort: 8080
        env:
        - name: SPRING_APPLICATION_NAME
          valueFrom:
            configMapKeyRef:
              name: eazybank-configmap
              key: ACCOUNTS_APPLICATION_NAME
        - name: SPRING_PROFILES_ACTIVE
          valueFrom: 
            configMapKeyRef:
              name: eazybank-configmap
              key: SPRING_PROFILES_ACTIVE
        - name: MANAGEMENT_ZIPKIN_TRACING_ENDPOINT
          valueFrom: 
            configMapKeyRef:
              name: eazybank-configmap
              key: MANAGEMENT_ZIPKIN_TRACING_ENDPOINT
        - name: SPRING_CONFIG_IMPORT
          valueFrom: 
            configMapKeyRef:
              name: eazybank-configmap
              key: SPRING_CONFIG_IMPORT
        - name: EUREKA_CLIENT_SERVICEURL_DEFAULTZONE
          valueFrom: 
            configMapKeyRef:
              name: eazybank-configmap
              key: EUREKA_CLIENT_SERVICEURL_DEFAULTZONE
---
apiVersion: v1
kind: Service
metadata:
  name: accounts-service
spec:
  selector:
    app: accounts
  type: LoadBalancer
  ports:
    - protocol: TCP
      port: 8080
      targetPort: 8080
```
- Run the command **kubectl apply -f 1_configmaps.yml** to create ConfigMaps inside Kubernetes cluster. The same can be validated by running the command **kubectl get configmap** 
- Run the commands **kubectl apply -f 2_zipkin.yml**, **kubectl apply -f 3_configserver.yml**, **kubectl apply -f 4_eurekaserver.yml**, **kubectl apply -f 5_accounts.yml** to deploy the applicable microservices into kubernetes cluster. The same can be validated by running the commands **kubectl get pods**,  **kubectl get deployments**, **kubectl get services**, **kubectl get replicaset**. 
- Like we discussed in the course, validate if the deployment of microservices is successful or not by using the endpoint IPs provided by kubernetes.
- Validate the automatic self healing, automatic roll out & roll back, autoscaling etc. inside the kubernetes cluster by following the steps and commands mentioned in the course.
- Atlast, make sure to delete the kubernetes cluster to avoid bill on your credit card :)

---
### HURRAY !!! Congratulations, you successfully setup and explored automatic self-healing, scaling, deployments, rollouts, rollbacks inside microservices network using Kubernetes
---
