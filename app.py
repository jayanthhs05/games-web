import os

from flask import Flask, render_template, request, redirect
from gen_question import gen_question

app = Flask(__name__)
app.config['quest'] = [[0 for _ in range(9)] for _ in range(9)]
app.config['ans'] = [[0 for _ in range(9)] for _ in range(9)]

@app.route('/', methods=["GET"])
def index():
    l = request.form.get("level")
    x = gen_question(1)
    app.config['ans'] = x[0]
    app.config['quest'] = x[1]
    return render_template('sudoku.html', grid=app.config['quest'])

@app.route('/submit', methods=["POST"])
def check():
    grid = app.config['quest']
    for i in range(9):
        for j in range(9):
            if grid[i][j] == 0:
               s = "in" + str(i) + str(j)
               grid[i][j] = int(request.form.get(s))
            if grid[i][j] != app.config['ans'][i][j]:
                return redirect('/lose')
    return redirect('/win')

@app.route('/win')
def winning():
    return render_template('win.html')

@app.route('/lose')
def losing():
    return render_template('lose.html')

if __name__ == '__main__':
    port = int(os.environ.get('PORT', 5000))
    debug = os.environ.get('FLASK_DEBUG', '0') == '1'
    app.run(host='0.0.0.0', port=port, debug=debug)
