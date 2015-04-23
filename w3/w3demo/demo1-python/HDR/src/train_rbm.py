import numpy
from sklearn import metrics
from sklearn.grid_search import GridSearchCV
from sklearn.linear_model import LogisticRegression
from sklearn.pipeline import Pipeline
from sklearn.neural_network import BernoulliRBM

digits = numpy.loadtxt(fname="optdigits.tra", delimiter=',')
n_samples = len(digits)

data = digits[:,:-1] / 16.0
target = digits[:,-1]

param_grid = {
    'rbm1__n_components': [36, 25, 16],
    'rbm2__n_components': [16],
    'rbm3__n_components': [9],
    'lr__penalty': ['l2', 'l1'],
    'lr__C': [1, 10, 100]
}

steps = [
    ('rbm1', BernoulliRBM()), 
    ('rbm2', BernoulliRBM()), 
    ('rbm3', BernoulliRBM()), 
    ('lr', LogisticRegression())
]

pipeline = Pipeline(steps)

grid_search = GridSearchCV(pipeline, param_grid, n_jobs = -1, verbose = 1, cv = 3)

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
