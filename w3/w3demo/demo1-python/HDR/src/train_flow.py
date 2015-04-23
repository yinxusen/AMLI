import mdp
import numpy
from sklearn import metrics

digits = numpy.loadtxt(fname="optdigits.tra", delimiter=',')
n_samples = len(digits)

data = digits[:,:-1]
target = digits[:,-1]

n_trains = n_samples / 3 * 2

train_data = [data[:n_trains, :]]
train_data_with_labels = [(data[:n_trains, :], target[:n_trains])]

test_data = data[n_trains:, :]
test_labels = target[n_trains:]

flow = mdp.Flow([mdp.nodes.PCANode(output_dim=25, dtype='f'),
    mdp.nodes.PolynomialExpansionNode(3),
    mdp.nodes.PCANode(output_dim=0.99),
    mdp.nodes.FDANode(output_dim=9),
    mdp.nodes.LogisticRegressionScikitsLearnNode()], verbose=True)

flow.train([train_data, None, train_data, train_data_with_labels, train_data_with_labels])

flow[-1].execute = flow[-1].label

prediction = flow(test_data)

print metrics.classification_report(test_labels, prediction)
