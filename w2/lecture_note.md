# week 2 lecture note

第二周主要介绍广义线性模型（Generalized Linear Model）。由于GLM在MLlib中实现比较简单，因此本周会讲大部分时间花在GLM的理论推导上。这会与下周的课有比较明显的区别。周三的课会偏重MLlib中ALS的实现部分。

MLlib中实现的广义线性模型有5中，分别是

－ Logistic regression

－ Linear regression

－ SVM

－ Lasso

－ Linear regression

再算上我们上周学到的Perceptron，目前我们一共会了解6中GLMs。这次课程的目的一是让大家了解GLM的特点，总结出规律，二是了解其在MLlib中的实现方法。更进一步，如果大家能对MLlib中的GLM实现提出自己的看法那就最好里。

## Logistic regression

首先介绍逻辑回归。跟之前的Perceptron不同，逻辑回归不再是学习一个分类函数，而是学习一个用于分类的条件概率$P(y|x)$，即在给定$x$的条件下，求解$y$取值的概率。
