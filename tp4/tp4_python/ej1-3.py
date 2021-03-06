from dataclasses import dataclass
import json
import sys
import numpy as np
from matplotlib import pyplot as plt
from typing import Any, Dict, List, Union
from matplotlib import cm



def main(data_path):
    with open(data_path, 'r') as particles_fd:
        rounds: List[List[float]] = json.load(particles_fd)

    errors = np.array(list(map(lambda r: np.array(r), rounds)))
    
    factor = 1_000
    count = 100

    t = [1e-1, 1e-2, 1e-3, 1e-4, 1e-5, 1e-6, 1e-7, 1e-8]
    
    fig = plt.figure(figsize=(10, 10))
    ax = fig.add_subplot(1, 1, 1)
    ax.set_yscale('log')
    ax.grid(which="both")
    ax.set_xscale('log')
    ax.set_xlabel(r'$d_t$ (s)', size=20)
    ax.set_ylabel(r'Error cuadratico medio ($m^2$)', size=20)
    labels = ['Verlet', 'Beemam', 'Gear']

    for i in range(len(errors)):
        ax.plot(t[1:], errors[i][1:len(t)], label=labels[i], marker='o')

    # ax.xaxis.set_major_formatter(MathTextSciFormatter("%1.3e"))
    # ax.yaxis.set_major_formatter(MathTextSciFormatter("%1.4e"))
    ax.tick_params(labelsize=16)
    plt.legend(fontsize=14)
    plt.show()


if __name__ == '__main__':
    if len(sys.argv) < 2:
        raise ValueError('Config path must be given by argument')
    try:
        main(sys.argv[1])
    except KeyboardInterrupt:
        pass
