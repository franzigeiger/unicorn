#!/usr/bin/python

from pylab import *
import numpy as na
import matplotlib.font_manager
import csv
import sys

plot_size=(9,10)
plot_ymin=100
whisker_factor=3
outlier_sym='.'

def round_base(x, base=10):
  f = 1
  while f * base <= x:
    f *= base
  return f

elapsed = {}
# Parse the CSV files
for file in sys.argv[1:]:
  threads = int(file.split('/')[-1].split('_')[0][:-1])
  #threads = int(file.split('-')[0][:-1])
  for row in csv.DictReader(open(file)):
    if (not row['label'] in elapsed):
      elapsed[row['label']] = {}
    if (not threads in elapsed[row['label']]):
      elapsed[row['label']][threads] = []
    elapsed[row['label']][threads].append(int(row['elapsed']))

# Draw a separate figure for each label found in the results.
for label in elapsed:
  # Transform the lists for plotting
  plot_data = []
  plot_labels = []
  column = 1
  min_val = 1000
  for thread_count in sort(list(elapsed[label])):
    for val in elapsed[label][thread_count]:
      if val < min_val:
        min_val = val
    plot_data.append(elapsed[label][thread_count])
    plot_labels.append(thread_count)
    column += 1


  # Start a new figure
  fig = figure(figsize=plot_size)

  # Pick some colors
  palegreen = matplotlib.colors.colorConverter.to_rgb('#8CFF6F')
  paleblue = matplotlib.colors.colorConverter.to_rgb('#708DFF')

  # Plot response time
  ax1 = fig.add_subplot(111)
  ax1.set_yscale('log')
  bp = boxplot(plot_data, notch=0, sym=outlier_sym, vert=1, whis=whisker_factor)

  # Tweak colors on the boxplot
  plt.setp(bp['boxes'], color='g')
  plt.setp(bp['whiskers'], color='g')
  plt.setp(bp['medians'], color='black')
  plt.setp(bp['fliers'], color='g', marker='.')

  # Now fill the boxes with desired colors
  numBoxes = len(plot_data)
  medians = range(numBoxes)
  for i in range(numBoxes):
    box = bp['boxes'][i]
    boxX = []
    boxY = []
    boxCoords = []
    for j in range(5):
      boxCoords.append([box.get_xdata()[j], box.get_ydata()[j]])
    boxPolygon = Polygon(boxCoords, facecolor=palegreen)
    ax1.add_patch(boxPolygon)

  # Label the axis
  ax1.set_title(label)
  ax1.set_xlabel('Number of concurrent requests')
  ax1.set_ylabel('Milliseconds')
  ax1.set_xticklabels(plot_labels)
  #fig.subplots_adjust(top=0.9, bottom=0.15, right=0.85, left=0.15)

  # Turn off scientific notation for Y axis
  ax1.yaxis.set_major_formatter(ScalarFormatter(False))

  # Set the lower y limit to the match the first column
  ax1.set_ylim(ymin=round_base(min_val, base=10))

  # Draw some tick lines
  ax1.yaxis.grid(True, linestyle='-', which='major', color='grey')
  ax1.yaxis.grid(True, linestyle='-', which='minor', color='lightgrey')
  # Hide these grid behind plot objects
  ax1.set_axisbelow(True)

  # Write the PNG file
  savefig(label[:2] + '.eps', format='eps')
