# Classification of Handwritten Digits

## Summary

In the past few days, I tried to use several classification algorithms (and their combinations) to classify the handwritten digits. For your convenient, I put the summary here, and details in the following sections.

There are three kinds of methods that I used:

- Single algorithms, e.g. SVM and Logistic Regression.

- Ensemble algorithm, i.e. Random Forest.

- Pipelined algorithms.

I put the results as follows, and I will explain them in detail in the following sections, including why I choose them, how to train them, etc.

| Method | Precision Avg | Recall Avg | F-measure Avg | Algorithms |
|:----------:|:----------:|:----------:|:----------:|:----------:|
| SVM | 0.99 | 0.99 | 0.99 | Single SVM + 5-fold CV |
| Logistic Regression | 0.96 | 0.96 | 0.96 | Single LR + 5-fold CV |
|Pipelined SVM | 0.99 | 0.99 | 0.99 | PCA + Ploynomial Expansions + PCA + FDA + SVM |
|Pipelined Logistic Regression | 0.99 | 0.99 | 0.99 | PCA + Ploynomial Expansions + PCA + FDA + LR |
|Random Forest | 0.98 | 0.98 | 0.98 | Single RF + 3-fold CV |
|Pipelined Random Forest | 0.96 | 0.96 | 0.96 | Polynomial Expansion + RF | 
|Layered Neural Network I | 0.95 | 0.95 | 0.95 | 2-layer RBM + LR + 3-fold CV |
|Layered Neural Network II | 0.90 | 0.90 | 0.90 | 3-layer RBM + LR + 3-fold CV |
|Layered Neural Network III | 0.83 | 0.83 | 0.83 | 4-layer RBM + LR + 3-fold CV |
|K-Nearest Neighbors | 0.98 | 0.98 | 0.98 | single KNN + 3-fold CV |

## Conclusion

In the handwritten digits recognition scenario, SVM is the best candidate. Logistic regression with manual polynomial expansions can compete with SVM and theoretically, they are very similar. Random forest gets the similar result with KNN. Because in the *classification superplane* point of view, they are very similar. Both KNN and random forest can draw an irregular division surface. I want to use different distance metrics (such as Isomap, MDS) for KNN, in order to find better *neighbors*, but it failed in this scenario. Neural network does not fit my expectation, but it is reasonable. 2-layer RBM plays the *nonlinear transformation* role, so I get a well enough result. But for 3-layer and 4-layer, I need to fine-tune the parameters of RBM layers along with LR layer to get better results, which scikit-learn does not support that.

# Details

## Some statistics

### The distribution of 0-9 samples

`awk -F "," '{print $65}' optdigits.tra | sort -n | uniq -c`

> 376 0

> 389 1

> 380 2

> 389 3

> 387 4

> 376 5

> 377 6

> 387 7

> 380 8

> 382 9

It looks good that the distribution of 0-9 digits looks like a uniform distribution.

## Methodology

### How to choose models

This dataset is quite small, so I will not try to use some **heavy** classifiers such as **Deep Neural Network**, which could cause over-fitting and cannot perform well on test set. But, the traditional **Shallow Neural Network** is a good idea, say, a network with two/three layers of neurons.

Instead of NN, I will try to use **Logistic Regression** and **SVM** first, to see whether can I get a good result. For the sake of low dimensionality, I might use some **dimension reduction** method to filter the dataset, e.g. **PCA**.

If time permits, I will try to use some **uncommon** methods, such as **random forest** and **k nearest neighbor**.

### How to do multi-classification

There are three methods to solve multi-classification problem:

1. 1 vs. (k-1) classification, namely, transforming a k-classification problem into k binary classification problem.

2. 1 vs. 1 k-classification classifier, such as **softmax regression** instead of **logistic regression**.

3. Error-correcting output codes, which is an uncommon way to solve multi-classification problem.

### How to do ETL of the dataset

In order to use k-fold cross validation, I will let open source tool do it. Scikit-learn is a good choice. Moreover, I will try to use manual feature expansions such as polynomial expansions, and dimension reduction methods such as PCA and FDA.

### How to do the grid search for hyper-parameters

Scikit-learn provides `GridSearchCV` methods to do the search. It is stable for single algorithms. But for pipelined methods, the search space is very large, it may cause OOM.

### How to choose open source tools

- Spark/MLlib is the most familiar tool of me, but it is too heavy and no necessary in the scenario;

- Scikit-learn seems the most suitable tool, I will try to use ETL part and classification part of it;

- MDP (Modular Data Process) is useful for a DAG style data process `Flow`, but scikit-learn also has the similar kind of API called `Pipeline`.

- LibSVM and libLinear are much faster than scikit-learn, but for a demo project, I prefer Python, because the scale-out and scale-up capabilities are not my first consideration.

## Details

### Install scikit-learn

`sudo apt-get install python-sklearn`

I try to use scikit-learn, with its SVM and Logistic Regression, and get good results.

For SVM, I get

> \>\>\> print(metrics.classification_report(expected, predicted))

>             precision    recall  f1-score   support

>         0.0       1.00      1.00      1.00       190

>         1.0       0.98      0.99      0.99       194

>         2.0       0.99      1.00      0.99       186

>         3.0       0.99      0.96      0.97       192

>         4.0       0.99      0.99      0.99       202

>         5.0       0.98      0.99      0.99       194

>         6.0       0.99      0.99      0.99       184

>         7.0       0.99      0.99      0.99       188

>         8.0       0.99      0.99      0.99       201

>         9.0       0.97      0.98      0.98       181

> avg / total       0.99      0.99      0.99      1912

For Logistic Regression, I get

> \>\>\> print(metrics.classification_report(expected, lrpredicted))

>              precision    recall  f1-score   support

>         0.0       0.99      1.00      0.99       190

>         1.0       0.93      0.95      0.94       194

>         2.0       0.98      0.97      0.98       186

>         3.0       0.98      0.93      0.96       192

>         4.0       0.98      0.97      0.97       202

>         5.0       0.97      0.97      0.97       194

>         6.0       0.98      0.99      0.98       184

>         7.0       0.99      0.99      0.99       188

>         8.0       0.93      0.93      0.93       201

>         9.0       0.91      0.94      0.93       181

> avg / total       0.96      0.96      0.96      1912

### Install MDP

`sudo aptitude install python-mdp`

Let's try something of the **flow**. I love this kind of **pipeline**. Here is the result:

>           precision    recall  f1-score   support

>         0.0       1.00      0.99      1.00       130

>         1.0       0.99      0.98      0.98       130

>         2.0       1.00      0.99      1.00       119

>         3.0       0.98      1.00      0.99       129

>         4.0       0.99      0.98      0.99       130

>         5.0       0.99      1.00      1.00       128

>         6.0       0.99      1.00      1.00       124

>         7.0       0.99      0.98      0.99       126

>         8.0       0.97      0.99      0.98       139

>         9.0       0.98      0.98      0.98       120

> avg / total       0.99      0.99      0.99      1275

We can see that it is even better than the former SVM result.

To testify my assumption, I substitude `SVCScikitLearnNode` with `LogisticRegressionScikitLearnNode`, and get similar result:

>             precision    recall  f1-score   support

>         0.0       1.00      0.99      1.00       130

>         1.0       0.98      0.98      0.98       130

>         2.0       1.00      1.00      1.00       119

>         3.0       0.98      0.99      0.99       129

>         4.0       0.99      0.98      0.99       130

>         5.0       0.98      1.00      0.99       128

>         6.0       0.99      1.00      1.00       124

>         7.0       0.99      0.98      0.99       126

>         8.0       0.99      0.98      0.98       139

>         9.0       0.99      0.98      0.99       120

> avg / total       0.99      0.99      0.99      1275

So, in the handwritten digits recognition scenario, logistic regression with some feature expansion and transformation can compete SVM. SVM uses **kernel trick** to substitute the manual feature expansion and transformation.

### Logistic Regression

Add k-fold cross validation and grid search in Logistic Regression. Result:

> Best score: 0.964

> Best parameters set:

> {'C': 0.1, 'intercept_scaling': 1, 'fit_intercept': True, 'penalty': 'l2', 'random_state': None, 'dual': False, 'tol': 0.0001, 'class_weight': None}

>              precision    recall  f1-score   support

>         0.0       1.00      1.00      1.00       130

>         1.0       0.93      0.95      0.94       130

>         2.0       0.99      0.93      0.96       119

>         3.0       0.96      0.97      0.97       129

>         4.0       0.98      0.95      0.96       130

>         5.0       0.97      0.99      0.98       128

>         6.0       0.99      0.99      0.99       124

>         7.0       0.98      0.98      0.98       126

>         8.0       0.91      0.92      0.91       139

>         9.0       0.94      0.94      0.94       120

> avg / total       0.96      0.96      0.96      1275

It looks better than the previous single Logistic Regression result.

### SVM

Let's add k-fold cross validation and grid search in SVM. Here is the result:

> Best score: 0.990

> Best parameters set:

> {'kernel': 'rbf', 'C': 10, 'verbose': False, 'probability': False, 'degree': 3, 'shrinking': True, 'max_iter': -1, 'random_state': None, 'tol': 0.001, 'cache_size': 200, 'coef0': 0.0, 'gamma': 0.001, 'class_weight': None}

>              precision    recall  f1-score   support

>         0.0       1.00      1.00      1.00       130

>         1.0       0.98      0.98      0.98       130

>         2.0       1.00      1.00      1.00       119

>         3.0       0.99      0.98      0.99       129

>         4.0       0.98      0.99      0.99       130

>         5.0       0.99      1.00      1.00       128

>         6.0       0.99      0.99      0.99       124

>         7.0       0.99      0.98      0.99       126

>         8.0       0.99      0.99      0.99       139

>         9.0       0.98      0.99      0.99       120

> avg / total       0.99      0.99      0.99      1275

### Neural Network

With the help of RBM and `GridSearchCV`, I can get the following result on LR. (To use `BernoulliRBM`, we should transform the features into [0,1].)

> Best score: 0.955

> Best parameters set:

> {'rbm1__batch_size': 10, 'lr__dual': False, 'rbm1__verbose': False, 'rbm1__n_iter': 10, 'rbm1': BernoulliRBM(batch_size=10, learning_rate=0.1, n_components=36, n_iter=10,

>        random_state=None, verbose=False), 'rbm1__n_components': 36, 'lr__tol': 0.0001, 'lr__class_weight': None, 'lr': LogisticRegression(C=100, class_weight=None, dual=False, fit_intercept=True,

>           intercept_scaling=1, penalty='l1', random_state=None, tol=0.0001), 'rbm1__learning_rate': 0.1, 'rbm1__random_state': None, 'lr__fit_intercept': True, 'lr__penalty': 'l1', 'lr__random_state': None, 'lr__intercept_scaling': 1, 'lr__C': 100}

>              precision    recall  f1-score   support

>         0.0       0.98      1.00      0.99       130

>         1.0       0.98      0.96      0.97       130

>         2.0       0.99      0.95      0.97       119

>         3.0       0.91      0.94      0.92       129

>         4.0       0.99      0.97      0.98       130

>         5.0       0.93      0.96      0.95       128

>         6.0       0.98      0.97      0.97       124

>         7.0       0.92      0.95      0.93       126

>         8.0       0.90      0.93      0.91       139

>         9.0       0.90      0.83      0.87       120

> avg / total       0.95      0.95      0.95      1275

It is not very exciting, but it is a good solution. I also try to use three-layer Neural Network, but the result is not very well.

### Random Forest

It seems that the '9' is always hard to tell than '0'. How about random forest?

From random forest, I can get my best score here:

> Best score: 0.972

> Best parameters set:

> {'rf__bootstrap': True, 'rf__max_depth': None, 'rf__n_estimators': 90, 'rf__verbose': 0, 'rf__criterion': 'gini', 'rf__min_density': None, 'rf__min_samples_split': 2, 'rf__compute_importances': None, 'rf': RandomForestClassifier(bootstrap=True, compute_importances=None,

>             criterion='gini', max_depth=None, max_features='auto',

>             min_density=None, min_samples_leaf=1, min_samples_split=2,

>             n_estimators=90, n_jobs=1, oob_score=False, random_state=None,

>             verbose=0), 'rf__max_features': 'auto', 'rf__n_jobs': 1, 'rf__random_state': None, 'rf__oob_score': False, 'rf__min_samples_leaf': 1}

>              precision    recall  f1-score   support

>         0.0       0.99      0.99      0.99       130

>         1.0       0.98      0.98      0.98       130

>         2.0       1.00      0.98      0.99       119

>         3.0       0.95      0.97      0.96       129

>         4.0       0.98      0.99      0.99       130

>         5.0       0.98      0.99      0.98       128

>         6.0       0.98      0.99      0.99       124

>         7.0       0.98      0.98      0.98       126

>         8.0       0.99      0.97      0.98       139

>         9.0       0.96      0.93      0.94       120

> avg / total       0.98      0.98      0.98      1275


### K Nearest Neighbors

I try to use a non-linear embedding method before KNN, but barely no promotion. So I only provide the single KNN result here.

> Best score: 0.982

> Best parameters set:

> {'knn': KNeighborsClassifier(algorithm='auto', leaf_size=30, metric='minkowski',

>            n_neighbors=5, p=2, weights='distance'), 'knn__p': 2, 'knn__metric': 'minkowski', 'knn__weights': 'distance', 'knn__leaf_size': 30, 'knn__algorithm': 'auto', 'knn__n_neighbors': 5}

>              precision    recall  f1-score   support

>         0.0       1.00      1.00      1.00       130

>         1.0       0.97      0.98      0.98       130

>         2.0       1.00      0.99      1.00       119

>         3.0       0.98      0.97      0.97       129

>         4.0       0.99      0.99      0.99       130

>         5.0       0.98      0.99      0.98       128

>         6.0       0.99      1.00      1.00       124

>         7.0       0.96      0.98      0.97       126

>         8.0       0.99      0.96      0.98       139

>         9.0       0.95      0.94      0.95       120

> avg / total       0.98      0.98      0.98      1275

## References

1. [Comparing Classification Algorithms for Handwritten Digits](http://blog.quantitations.com/machine%20learning/2013/02/27/comparing-classification-algorithms-for-handwritten-digits/)

2. [Example: Handwritten Digit Classification](http://pythonhosted.org/bob.learn.boosting/example.html)

3. [Classification of handwritten digits using a SVM](http://nbviewer.ipython.org/url/www.hdm-stuttgart.de/~maucher/ipnotebooks/MachineLearning/svmDigitRecognition.ipynb)

4. [Using neural nets to recognize handwritten digits](http://neuralnetworksanddeeplearning.com/chap1.html)

5. [Recognizing hand-written digits](http://scikit-learn.org/stable/auto_examples/plot_digits_classification.html)

6. [The MNIST Database of Handwritten Digits](http://yann.lecun.com/exdb/mnist/)

7. [Modular Toolkit for Data Processing](http://mdp-toolkit.sourceforge.net/documentation.html)

8. [Scikit-learn document](http://scikit-learn.org/stable/)

9. [Handwritten digits classification with MDP and scikits.learn](http://mdp-toolkit.sourceforge.net/examples/scikits_learn/digit_classification.html)
