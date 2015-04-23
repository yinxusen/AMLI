import numpy
from sklearn import metrics
from sklearn.grid_search import GridSearchCV
from sklearn.pipeline import Pipeline
from sklearn.neighbors import KNeighborsClassifier

digits = numpy.loadtxt(fname="optdigits.tra", delimiter=',')
n_samples = len(digits)

data = digits[:,:-1]
target = digits[:,-1]

param_grid = {
    'knn__weights': ['uniform', 'distance']
}

steps = [
    ('knn', KNeighborsClassifier())
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
