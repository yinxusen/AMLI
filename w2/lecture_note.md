<head>
    <script type="text/javascript" src="http://cdn.mathjax.org/mathjax/latest/MathJax.js?config=default"></script>
</head>

# week 2 lecture note

第二周主要介绍广义线性模型（Generalized Linear Model）。由于GLM在MLlib中实现比较简单，因此本周会讲大部分时间花在GLM的理论推导上。这会与下周的课有比较明显的区别。周三的课会偏重MLlib中ALS的实现部分。

MLlib中实现的广义线性模型有5中，分别是

- Logistic regression

- Linear regression

- SVM

- Lasso

- Linear regression

再算上我们上周学到的Perceptron，目前我们一共会了解6种GLMs。这次课程的目的一是让大家了解GLM的特点，总结出规律，二是了解其在MLlib中的实现方法。更进一步，如果大家能对MLlib中的GLM实现提出自己的看法那就最好了。

## Logistic regression

首先介绍逻辑回归。跟之前的Perceptron不同，逻辑回归不再是学习一个分类函数，而是学习一个用于分类的条件概率$P(y|x)$，即在给定$x$的条件下，求解$y$取值的概率。

更新一下我们的符号，我们令$p_y(x;w)$代表我们条件概率的取值，其中$w$是我们模型的参数。并假设我们的$y$只有两个取值$\{0, 1\}$。给出两个不同取值的概率分别为

$$p_1(x;w) = \frac{1}{1+exp(-w^Tx)}$$

$$p_0(x;w) = \frac{exp(-w^Tx)}{1+exp(-w^Tx)}$$

从定义中容易得出

$$log\frac{p_0}{p_1} = -w^Tx$$

在某种程度上说明了``线性''的来源。

那么为什么会选择这种形式的函数作为我们概率的取值呢？从logit函数曲线的形状可见端倪。线性函数$-w^Tx$的取值范围是正负无穷，而logit函数可以把正负无穷的区间映射到$[0, 1]$之间，比较符合概率的取值范围。

给定了假设和模型，下面要通过优化损失函数得到参数的最优解。在这里我们使用负的log loss：

$$L(\hat{P}(y|x), y) = \left\{
   \begin{array}{c}
   -log P(y=1|x_i)\qquad if ~x_i == 1,  \\
   -logP(y=0|x_i)\qquad if ~ x_i == 0.  \\
   \end{array}
  \right.$$

为了最小化损失函数，我们要做的是最大化正的log概率，其实等价于最大似然，即

$$max_w\sum_ilog\hat{P}(y_i|x_i)$$

对于最大似然最直观的理解就是，如果一个变量$x_i$的类别为1，那就让$\hat{P}(y_i=1|x_i)$的概率远大于$\hat{P}(y_i=0|x_i)$的概率，反之亦然。

但是上文中的函数是分段函数，不便求梯度，因此我们利用概率的一些法则将分段函数展开成一个函数

$$l(w) = \sum_ip_1(x;w)*y_i + (1-p_1(x;w))*(1-y_i)$$

大家可以将$y_i=1$和$y_i=0$带入到上面的式子中自行验证其正确性。





































































































































































































































































































































































































































































































































































































































































