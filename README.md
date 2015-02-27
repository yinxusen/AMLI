# Applied Machine Learning and Implementation

## Overview

12 weeks, 2 hours / per week

20 min per episode, so six episodes per week.

This course will cover:

\*\*\*\*\* **Spark MLlib**

\*\*\*\* **ML Pipeline and GraphX**

\*\*\* **Spark Core and Spark SQL**

\*\* **Spark Streaming**

\* **Scikit-learn for reference.**

## Textbooks

1. Advanced Analytics with Spark
2. Machine Learning with Spark
3. The Lion Way: Machine Learning plus Intelligent Optimization
4. Others...

## week 1 Introduction

1. Spark ABC
2. Machine learning ABC
3. Graph Computing ABC
4. Demos for Spark, MLlib, and GraphX

## week 2 Generalized Linear Model

2. Logistic regression
3. Linear regression
4. SVM
5. LASSO
6. Ridge regression
7. Applied demos such as Handwritten digits recognition, etc.

## week 3 Recommendation

1. Recommendation ALS
2. Singular Value Decomposition
3. The implementation in both MLlib and Mahout
4. Applied demo of recommendation with PredictionIO.

## week 4 Clustering

1. k-means
2. LDA
3. Applied demo of geo-location clustering and topic modeling

## week 5 Streaming-wised Machine Learning

1. Lambda Architecture
2. Parameter Server
3. Several algorithms from Freeman labs
4. Applied demo such as the zebrafish experiment

## week 6 ML Pipeline

1. Pipeline of Scikit-learn
2. Pipeline of Spark (DataFrame, ML Pipeline, etc.)
3. Applied demo (TBD)

## week 7 Scientific Computing

1. Scientific computing and Notices from Matrix Computation
2. Matrix libs (in C/Fortran and Java)
3. Matrix in MLlib
4. Applied demo (TBD)

## week 8 The Graph Computation Model

1. Graph computing and libs
2. revisit LDA, ALS
3. Applied demo such as community detection for food network/recommendation.

## week 9 Tree Model and Boosting

1. Tree model
2. Random forest
3. Ensemble in Kaggle and practice
4. Applied demo for ensemble

## week 10 Evaluation

1. Evaluation methods
2. Implementations in MLlib
3. Online / Offline evaluations

## week 11 Optimization in Parallel

1. Commonly used optimization algorithms
2. Sequential gene of optimization algorithms
3. BSP model to BSP+ model to SSP
4. Future ways?

## week 12 Rethink of practical machine learning and how to build a good system

1. One, two, three of practical ML
2. Rethink of practical machine learning
3. How to build a great machine learning system?
4. Compare with Mahout / Oryx2 / VM / ...

## Survey of Advanced Analytics with Spark

| Chapter | Topic | Algorithms | Dataset | Source |
|:-----:|:-----:|:-----:|:-----:|:-----:|:-----:|
| 2 | Record Linkage | Entity resolution, record dedup, merge-and-purge, list washing | Some business data such as TCPDS | UCI ML repo |
| 3 | Recommending | ALS | Who plays what or who rates what | Audioscrobbler |
| 4 | Predicting Forest Cover | Decision Tree | The type of forest covering parcels of land in Colorado | UCI ML repo |
| 5 | Anomaly detection in network traffic | K-means | Network intrusion data | KDD Cup 1999 Dataset |
| 6 | Understanding wikipedia | Latent Semantic Analysis, SVD, TF-IDF, etc | wikipedia texts | wikipedia |
| 7 | Analyzing Co-occurrence Networks | Massive graph algorithms in GraphX | MEDLINE citation index | US National Library of Medicine |
| 8 | Geo and Temporal data analysis | Building sessions | New York Taxicab Data | New York City Taxi and Limousine Commission |
| 9 | Estimating Finacial Risk | Monte Carlo Simulation | Stock Data | Yahoo! |
| 10 | Analyzing Genomic Data | Massive genome analysis algorithms | Genome data | NCBI |
| 11 | Analyzing Neuroimaging Data | Thunder | Images of zebrafish brains | Thunder repository |

## Structure of directories

/src/chapterx --> The code snippets of each chapter

/src/chapterx/{java, python, scala} --> Code snippets written with Mahout, Scikit-learn, and Spark

## Spark VS Scikit-learn

### Algorithms

| Type | Algorithm | Scikit-learn | Spark |
|:-----------:|:----------:|:----------:|:----------:|
|Classification| Logistic Regression | YES | YES
|Classification| Perceptron | YES |
|Classification| Passive Aggressive Algorithms | YES
|Classification| SVM | YES | YES
|Classification| Naive Bayes | YES | YES
|Classification| Decision Tree | YES | YES
|Classification| Ensemble methods | YES | YES
|Classification| Label Propogation | YES | YES (in GraphX)
|Classification| LDA and QDA | YES |
|Regression| Ordinary Least Square | YES | YES
|Regression| Ridge Regression | YES | YES
|Regression| LASSO | YES | YES
|Regression| Elastic Net | YES
|Regression| Multi-task LASSO | YES 
|Regression| Least Angle Regression | YES
|Regression| LARS LASSO | YES
|Regression| Orthogonal Matching Pursuit | YES
|Regression| Bayesian Regression | YES
|Regression| Polynomial Regression | YES
|Regression|  Nearest Neighbor | YES | YES
|Regression| Gaussian Process | YES
|Regression| Isotonic Regression | YES
|Clustering| K-means | YES | YES
|Clustering| Affinity Propagation | YES
|Clustering| Mean shift | YES
|Clustering| Spectral Clustering | YES
|Clustering| Ward | YES
|Clustering| Agglomerative clustering | YES
|Clustering| DBSCAN | YES
|Clustering| Gaussian Mixtures | YES
|Dimension Reduction| PCA | YES | YES
|Dimension Reduction| SVD / LSA | YES | YES
|Dimension Reduction| Dictionary Learning | YES
|Dimension Reduction| Factor Analysis | YES
|Dimension Reduction| ICA | YES
|Dimension Reduction| NMF | YES
|Model Selection| Cross Validation | YES | YES
|Model Selection| Grid Search | YES
|Model Selection| Pipeline | YES | YES
|Model Selection| Feature Union | YES | YES
|Model Selection| Model Evaluation | YES | YES
|Model Selection| Model Presistence | YES
|Model Selection| Validation Curves | YES 
|Preprocessing| Standardization | YES | YES
|Preprocessing| Encoding categorical features | YES | YES (dependency)
|Preprocessing| Binarization | YES
|Preprocessing| Normalization | YES | YES
|Preprocessing| Label preprocessing | YES
|Preprocessing| Imputation of missing values | YES
|Preprocessing| Unsupervised data reduction | YES
