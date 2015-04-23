import numpy
from sklearn import datasets, svm, metrics
from sklearn.grid_search import GridSearchCV
from sklearn.linear_model import LogisticRegression
from sklearn.pipeline import Pipeline
from sklearn.decomposition import PCA
from sklearn.preprocessing import PolynomialFeatures
from sklearn.lda import LDA

digits = numpy.loadtxt(fname="optdigits.tra", delimiter=',')
n_samples = len(digits)

data = digits[:,:-1]
target = digits[:,-1]

param_grid = {
    'pca1__n_components': [16],
    'poly__degree': [2],
    'pca2__n_components': [0.8],
    'lda__n_components': [9],
    'lr__penalty': ['l2'],
    'lr__C': [0.1, 1]
}

steps = [('pca1', PCA()), 
    ('poly', PolynomialFeatures()),
    ('pca2', PCA()), 
    ('lda', LDA()),
    ('lr', LogisticRegression())]

pipeline = Pipeline(steps)

grid_search = GridSearchCV(pipeline, param_grid, n_jobs = -1, verbose = 1, cv = 2)

n_trains = n_samples / 3 * 2

# We learn the digits on the first half of the digits
grid_search.fit(data[:n_trains], target[:n_trains])

print("Best score: %0.3f" % grid_search.best_score_)
print("Best parameters set:")
best_parameters = grid_search.best_estimator_.get_params()
print best_parameters

# Now predict the value of the digit on the second half:
expected = target[n_trains:]
predicted = grid_search.best_estimator_.predict(data[n_trains:])

print(metrics.classification_report(expected, predicted))
